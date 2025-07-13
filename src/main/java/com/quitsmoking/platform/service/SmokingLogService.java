package com.quitsmoking.platform.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitsmoking.platform.dto.HealthStatResponse;
import com.quitsmoking.platform.dto.SmokingLogRequest;
import com.quitsmoking.platform.dto.SmokingStatsSummary;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.repository.FreeQuitPlanRepository;
import com.quitsmoking.platform.repository.InitialConditionRepository;
import com.quitsmoking.platform.repository.QuitPlanRepository;
import com.quitsmoking.platform.repository.SmokingLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

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

    // Lấy plan active (free hoặc paid)
    public Object findActivePlan(Account acc) {
        QuitPlan paidPlan = quitPlanRepo.findByAccountAndStatus(acc, PlanStatus.ACTIVE).orElse(null);
        if (paidPlan != null) return paidPlan;
        FreeQuitPlan freePlan = freeQuitPlanRepo.findByAccountAndActiveTrue(acc).orElse(null);
        if (freePlan != null) return freePlan;
        throw new IllegalStateException("Không có kế hoạch active!");
    }

    // Ghi nhận log cho ngày hôm nay
    @Transactional
    public HealthStatResponse recordSmokingLog(Account acc, SmokingLogRequest req) {
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

        return calculateHealthStatForDay(acc, plan, today, log.getCigarettesToday());
    }

    // Lấy log và chỉ số của 1 ngày
    public HealthStatResponse getHealthStatOfDay(Account acc, LocalDate date) {
        Object plan = findActivePlan(acc);
        SmokingLog log;
        if (plan instanceof QuitPlan paidPlan) {
            log = logRepo.findByAccountAndQuitPlanAndDate(acc, paidPlan, date)
                    .orElseThrow(() -> new IllegalArgumentException("Chưa có ghi nhận ngày này"));
        } else if (plan instanceof FreeQuitPlan freePlan) {
            log = logRepo.findByAccountAndFreeQuitPlanAndDate(acc, freePlan, date)
                    .orElseThrow(() -> new IllegalArgumentException("Chưa có ghi nhận ngày này"));
        } else {
            throw new IllegalStateException("Plan không hợp lệ!");
        }
        return calculateHealthStatForDay(acc, plan, date, log.getCigarettesToday());
    }

    // Lấy thống kê tổng hợp cho plan active
    public SmokingStatsSummary getStats(Account acc) {
        Object plan = findActivePlan(acc);
        InitialCondition ic = initialRepo.findByAccount(acc).orElseThrow();
        List<SmokingLog> logs;
        if (plan instanceof QuitPlan paidPlan) {
            logs = logRepo.findAllByAccountAndQuitPlanOrderByDate(acc, paidPlan);
        } else if (plan instanceof FreeQuitPlan freePlan) {
            logs = logRepo.findAllByAccountAndFreeQuitPlanOrderByDate(acc, freePlan);
        } else {
            throw new IllegalStateException("Plan không hợp lệ!");
        }
        return calculateSummaryStats(ic, plan, logs);
    }

    // Tổng hợp log: tổng ngày, tổng điếu, tổng tiền, số ngày đạt mục tiêu
    private SmokingStatsSummary calculateSummaryStats(InitialCondition ic, Object plan, List<SmokingLog> logs) {
        int baseline = ic.getCigarettesPerDay();
        int pricePerCig = ic.getPricePerCigarette();

        int totalDays = logs.size();
        int totalCigarettes = 0;
        int totalMoneySaved = 0;
        int daysAchievedTarget = 0;

        List<Map<String, Object>> planDetails = null;
        if (plan instanceof QuitPlan paidPlan) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                planDetails = mapper.readValue(
                        paidPlan.getPlanDetail(), new TypeReference<List<Map<String, Object>>>() {});
            } catch (Exception e) {
                planDetails = null;
            }
        }
        for (SmokingLog log : logs) {
            totalCigarettes += log.getCigarettesToday();

            int target = 0;
            int dayIndex = 0;
            if (plan instanceof QuitPlan paidPlan) {
                dayIndex = (int) ChronoUnit.DAYS.between(paidPlan.getStartDate(), log.getDate());
                if (planDetails != null && dayIndex >= 0 && dayIndex < planDetails.size()) {
                    Object cigsObj = planDetails.get(dayIndex).get("cigarettes");
                    if (cigsObj instanceof Integer) {
                        target = (Integer) cigsObj;
                    } else if (cigsObj instanceof Number) {
                        target = ((Number) cigsObj).intValue();
                    }
                }
                if (log.getCigarettesToday() <= target) daysAchievedTarget++;
            }
            // FREE PLAN hoặc không có planDetail => target = 0 (tức là cứ hút là "vượt")
            int saved = (target - log.getCigarettesToday()) * pricePerCig;
            totalMoneySaved += saved;
        }

        SmokingStatsSummary stats = new SmokingStatsSummary();
        stats.setTotalDays(totalDays);
        stats.setTotalCigarettes(totalCigarettes);
        stats.setTotalMoneySaved(totalMoneySaved);
        stats.setDaysAchievedTarget(daysAchievedTarget);

        return stats;
    }

    // Lấy chỉ số sức khỏe của 1 ngày
    private HealthStatResponse calculateHealthStatForDay(Account acc, Object plan, LocalDate date, int cigarettesToday) {
        InitialCondition ic = initialRepo.findByAccount(acc).orElseThrow();
        int baseline = ic.getCigarettesPerDay();
        int pricePerCig = ic.getPricePerCigarette();
        int dayIndex = 0;
        int target = 0;

        if (plan instanceof QuitPlan paidPlan) {
            dayIndex = (int) ChronoUnit.DAYS.between(paidPlan.getStartDate(), date);
            target = extractTargetFromPlanDetail(paidPlan.getPlanDetail(), dayIndex);
        } else if (plan instanceof FreeQuitPlan freePlan) {
            dayIndex = (int) ChronoUnit.DAYS.between(freePlan.getStartDate(), date);
            target = 0; // Free plan không có target cụ thể
        }
        return HealthStatCalculator.calculate(cigarettesToday, target, baseline, pricePerCig);
    }

    private int extractTargetFromPlanDetail(String planDetailJson, int dayIndex) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> planDetails = mapper.readValue(
                    planDetailJson, new TypeReference<List<Map<String, Object>>>() {});
            if (dayIndex >= 0 && dayIndex < planDetails.size()) {
                Object cigsObj = planDetails.get(dayIndex).get("cigarettes");
                if (cigsObj instanceof Integer) {
                    return (Integer) cigsObj;
                } else if (cigsObj instanceof Number) {
                    return ((Number) cigsObj).intValue();
                }
            }
        } catch (Exception e) {
            // Log lỗi nếu muốn
        }
        return 0;
    }
}
