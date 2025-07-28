package com.quitsmoking.platform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitsmoking.platform.dto.HealthStatResponse;
import com.quitsmoking.platform.dto.SmokingLogRequest;
import com.quitsmoking.platform.dto.SmokingStatsSummary;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SmokingLogService {

    @Autowired
    private SmokingLogRepository logRepo;
    @Autowired
    private QuitPlanRepository quitPlanRepo;
    @Autowired
    private FreeQuitPlanRepository freeQuitPlanRepo;
    @Autowired
    private InitialConditionRepository initialRepo;
    @Autowired
    private PurchasedPlanRepository purchasedPlanRepo;
    @Autowired
    private DailyTaskRepository dailyTaskRepo;

    // Lấy plan active (free, paid, hoặc coach)
    public Object findActivePlan(Account acc) {
        QuitPlan paidPlan = quitPlanRepo.findByAccountAndStatus(acc, PlanStatus.ACTIVE).orElse(null);
        if (paidPlan != null) return paidPlan;

        FreeQuitPlan freePlan = freeQuitPlanRepo.findByAccountAndActiveTrue(acc).orElse(null);
        if (freePlan != null) return freePlan;

        PurchasedPlan coachPlan = purchasedPlanRepo.findFirstByAccountAndStatusAndCoachSupport(acc, PlanStatus.ACTIVE, true).orElse(null);
        if (coachPlan != null) return coachPlan;

        throw new IllegalStateException("Không có kế hoạch active!");
    }

    @Transactional
    public HealthStatResponse recordSmokingLog(Account acc, SmokingLogRequest req) {
        try {
            Object plan = findActivePlan(acc);
            LocalDate today = LocalDate.now();

            SmokingLog log;
            if (plan instanceof QuitPlan paidPlan) {
                log = logRepo.findByAccountAndQuitPlanAndDate(acc, paidPlan, today).orElse(new SmokingLog());
                log.setQuitPlan(paidPlan);
                log.setFreeQuitPlan(null);
            } else if (plan instanceof FreeQuitPlan freePlan) {
                log = logRepo.findByAccountAndFreeQuitPlanAndDate(acc, freePlan, today).orElse(new SmokingLog());
                log.setFreeQuitPlan(freePlan);
                log.setQuitPlan(null);
            } else if (plan instanceof PurchasedPlan coachPlan) {
                log = logRepo.findByAccountAndPurchasedPlanAndDate(acc, coachPlan, today).orElse(new SmokingLog());
                log.setPurchasedPlan(coachPlan);
                log.setQuitPlan(null);
                log.setFreeQuitPlan(null);
            } else {
                throw new IllegalStateException("Plan không hợp lệ!");
            }

            log.setAccount(acc);
            log.setDate(today);
            log.setCigarettesToday(req.getCigarettesToday());

            InitialCondition ic = initialRepo.findByAccount(acc).orElseThrow();
            int pricePerCig = ic.getPricePerCigarette();
            log.setPrice(req.getCigarettesToday() * pricePerCig);
            log.setNote(req.getNote());
            logRepo.save(log);

            HealthStatResponse response = calculateHealthStatForDay(acc, plan, today, log.getCigarettesToday());
            response.setNote(log.getNote());
            return response;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể ghi nhận log: " + e.getMessage(), e);
        }
    }

    public HealthStatResponse getHealthStatOfDay(Account acc, LocalDate date) {
        Object plan = findActivePlan(acc);
        SmokingLog log;

        if (plan instanceof QuitPlan paidPlan) {
            log = logRepo.findByAccountAndQuitPlanAndDate(acc, paidPlan, date)
                    .orElseThrow(() -> new IllegalArgumentException("Chưa có ghi nhận ngày này"));
        } else if (plan instanceof FreeQuitPlan freePlan) {
            log = logRepo.findByAccountAndFreeQuitPlanAndDate(acc, freePlan, date)
                    .orElseThrow(() -> new IllegalArgumentException("Chưa có ghi nhận ngày này"));
        } else if (plan instanceof PurchasedPlan coachPlan) {
            log = logRepo.findByAccountAndPurchasedPlanAndDate(acc, coachPlan, date)
                    .orElseThrow(() -> new IllegalArgumentException("Chưa có ghi nhận ngày này"));
        } else {
            throw new IllegalStateException("Plan không hợp lệ!");
        }

        HealthStatResponse response = calculateHealthStatForDay(acc, plan, date, log.getCigarettesToday());
        response.setNote(log.getNote());
        return response;
    }

    public SmokingStatsSummary getStats(Account acc) {
        Object plan = findActivePlan(acc);
        SmokingStatsSummary stats;

        if (plan instanceof QuitPlan paidPlan) {
            InitialCondition ic = initialRepo.findByAccount(acc).orElseThrow();
            List<SmokingLog> logs = logRepo.findAllByAccountAndQuitPlanOrderByDate(acc, paidPlan);
            stats = calculateSummaryStats(ic, plan, logs);
            stats.setFreePlan(false);
        } else if (plan instanceof FreeQuitPlan freePlan) {
            stats = new SmokingStatsSummary();
            stats.setFreePlan(true);
            stats.setTotalDays(0);
            stats.setTotalCigarettes(0);
            stats.setTotalMoneySaved(0);
            stats.setDaysAchievedTarget(0);
        } else if (plan instanceof PurchasedPlan coachPlan) {
            InitialCondition ic = initialRepo.findByAccount(acc).orElseThrow();
            List<SmokingLog> logs = logRepo.findAllByAccountAndPurchasedPlanOrderByDate(acc, coachPlan);
            stats = calculateSummaryStats(ic, plan, logs);
            stats.setFreePlan(false);
        } else {
            throw new IllegalStateException("Không có kế hoạch active!");
        }

        return stats;
    }

    private SmokingStatsSummary calculateSummaryStats(InitialCondition ic, Object plan, List<SmokingLog> logs) {
        int baseline = ic.getCigarettesPerDay();
        int pricePerCig = ic.getPricePerCigarette();

        int totalDays = logs.size();
        int totalCigarettes = 0;
        int totalMoneySaved = 0;
        int daysAchievedTarget = 0;

        boolean isPaidPlan = plan instanceof QuitPlan;
        boolean isCoachPlan = plan instanceof PurchasedPlan;

        List<DailyTask> tasks = null;
        if (isCoachPlan) {
            tasks = dailyTaskRepo.findAllByPurchasedPlanOrderByDateAsc((PurchasedPlan) plan);
        }

        for (SmokingLog log : logs) {
            totalCigarettes += log.getCigarettesToday();
            int target;

            if (isPaidPlan) {
                int dayIndex = (int) ChronoUnit.DAYS.between(((QuitPlan) plan).getStartDate(), log.getDate());
                target = extractTargetFromPlanDetail(((QuitPlan) plan).getPlanDetail(), dayIndex);
            } else if (isCoachPlan) {
                LocalDate logDate = log.getDate();
                Optional<DailyTask> taskOpt = tasks.stream()
                        .filter(t -> t.getDate().equals(logDate))
                        .findFirst();
                target = taskOpt.map(DailyTask::getTargetSmokePerDay).orElse(baseline);
            } else {
                target = baseline;
            }

            if (log.getCigarettesToday() <= target) daysAchievedTarget++;
            int saved = Math.max((target - log.getCigarettesToday()) * pricePerCig, 0);
            totalMoneySaved += saved;
        }

        SmokingStatsSummary stats = new SmokingStatsSummary();
        stats.setTotalDays(totalDays);
        stats.setTotalCigarettes(totalCigarettes);
        stats.setTotalMoneySaved(totalMoneySaved);
        stats.setDaysAchievedTarget(daysAchievedTarget);
        return stats;
    }

    private HealthStatResponse calculateHealthStatForDay(Account acc, Object plan, LocalDate date, int cigarettesToday) {
        InitialCondition ic = initialRepo.findByAccount(acc).orElseThrow();
        int baseline = ic.getCigarettesPerDay();
        int pricePerCig = ic.getPricePerCigarette();
        int target = 0;
        int daysCompleted = 0;
        int totalPlanDays = 0;

        if (plan instanceof QuitPlan paidPlan) {
            int dayIndex = (int) ChronoUnit.DAYS.between(paidPlan.getStartDate(), date);
            target = extractTargetFromPlanDetail(paidPlan.getPlanDetail(), dayIndex);
            daysCompleted = (int) ChronoUnit.DAYS.between(paidPlan.getStartDate(), LocalDate.now()) + 1;
            totalPlanDays = paidPlan.getPurchasedPlan().getPlanPackage().getDuration();
        } else if (plan instanceof FreeQuitPlan freePlan) {
            target = 0;
            daysCompleted = (int) ChronoUnit.DAYS.between(freePlan.getStartDate(), LocalDate.now()) + 1;
            totalPlanDays = (int) ChronoUnit.DAYS.between(freePlan.getStartDate(), freePlan.getEndDate()) + 1;
        } else if (plan instanceof PurchasedPlan coachPlan) {
            DailyTask task = dailyTaskRepo.findByPurchasedPlanAndDate(coachPlan, date)
                    .orElseThrow(() -> new IllegalArgumentException("Coach chưa giao nhiệm vụ hôm nay"));
            target = task.getTargetSmokePerDay();
            daysCompleted = (int) ChronoUnit.DAYS.between(coachPlan.getActivationDate(), LocalDate.now()) + 1;
            totalPlanDays = coachPlan.getPlanPackage().getDuration();
        } else {
            throw new IllegalStateException("Plan không hợp lệ!");
        }

        HealthStatResponse response = HealthStatCalculator.calculate(cigarettesToday, target, baseline, pricePerCig);
        response.setDaysCompleted(Math.max(daysCompleted, 0));
        response.setTotalPlanDays(totalPlanDays);
        return response;
    }

    private int extractTargetFromPlanDetail(String planDetailJson, int dayIndex) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> planDetails = mapper.readValue(planDetailJson,
                    new TypeReference<List<Map<String, Object>>>() {});
            if (dayIndex >= 0 && dayIndex < planDetails.size()) {
                Object cigsObj = planDetails.get(dayIndex).get("cigarettes");
                if (cigsObj instanceof Integer) {
                    return (Integer) cigsObj;
                } else if (cigsObj instanceof Number) {
                    return ((Number) cigsObj).intValue();
                }
            }
        } catch (Exception ignored) {}
        return 0;
    }
}
