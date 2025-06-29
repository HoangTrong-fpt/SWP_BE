package com.quitsmoking.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.entity.QuitPlan;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.InitialConditionRepository;
import com.quitsmoking.platform.repository.QuitPlanRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import com.quitsmoking.platform.entity.PurchasedPlan;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class QuitPlanService {
    @Autowired
    private QuitPlanRepository quitPlanRepository;
    @Autowired
    private InitialConditionRepository initialConditionRepository;
    @Autowired
    private AuthenticationRepository accountRepository;
    @Autowired
    private PurchasedPlanRepository purchasedPlanRepository;

    private Account getAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public QuitPlanResponse createQuitPlan(String username, QuitPlanRequest request) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

        if (quitPlanRepository.existsByAccountAndStatus(account, PlanStatus.ACTIVE)) {
            throw new IllegalStateException("Bạn đã có kế hoạch hoạt động!");
        }

        InitialCondition ic = initialConditionRepository.findByAccountAndIsActiveTrue(account)
                .orElseThrow(() -> new IllegalArgumentException("Bạn chưa khai báo điều kiện ban đầu!"));

        QuitPlan plan = new QuitPlan();
        plan.setAccount(account);
        plan.setInitialCondition(ic);
        plan.setStartDate(request.getStartDate());
        plan.setTargetQuitDate(request.getTargetQuitDate());
        plan.setGoal(request.getGoal());
        plan.setMotivationReason(request.getMotivationReason());
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setCreatedAt(LocalDate.now());

        PurchasedPlan purchasedPlan = null;

        if (request.getMethod() == MethodType.TEMPLATE) {
            // Tìm gói chưa dùng gần nhất
            purchasedPlan = purchasedPlanRepository.findByAccountAndUsedFalse(account)
                    .orElseThrow(() -> new IllegalStateException("Bạn cần mua gói trả phí để sử dụng kế hoạch mẫu"));

            if (purchasedPlan.getTemplateType().name().equalsIgnoreCase("FREE")) {
                throw new IllegalStateException("Gói FREE không được phép tạo kế hoạch mẫu (template)");
            }

            String templateType = purchasedPlan.getTemplateType().name(); // Enum -> String
            int totalDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getTargetQuitDate()) + 1;

            plan.setMethod(MethodType.TEMPLATE);
            plan.setPlanDetail(generateTemplatePlanDetail(ic.getCigarettesPerDay(), totalDays, templateType));
        } else if (request.getMethod() == MethodType.CUSTOM) {
            plan.setMethod(MethodType.CUSTOM);
            plan.setPlanDetail(request.getPlanDetail());
        } else {
            throw new IllegalArgumentException("Method không hợp lệ");
        }

        QuitPlan saved = quitPlanRepository.save(plan);

        if (purchasedPlan != null) {
            purchasedPlan.setUsed(true);
            purchasedPlan.setLinkedQuitPlan(saved);
            purchasedPlanRepository.save(purchasedPlan);
        }

        return mapToResponse(saved);
    }


    public QuitPlanResponse getActiveQuitPlan(String username) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

        QuitPlan plan = quitPlanRepository.findByAccountAndStatus(account, PlanStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("Không có kế hoạch hoạt động"));
        return mapToResponse(plan);
    }

    @Transactional
    public void cancelQuitPlan(String username, Long id) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));
        QuitPlan plan = quitPlanRepository.findByIdAndAccount(id, account)
                .orElseThrow(() -> new IllegalArgumentException("ID kế hoạch không hợp lệ"));
        plan.setStatus(PlanStatus.CANCELLED);
        quitPlanRepository.save(plan);
    }

    private QuitPlanResponse mapToResponse(QuitPlan plan) {
        QuitPlanResponse res = new QuitPlanResponse();
        res.setId(plan.getId());
        res.setInitialConditionId(plan.getInitialCondition().getId());
        res.setStartDate(plan.getStartDate());
        res.setTargetQuitDate(plan.getTargetQuitDate());
        res.setGoal(plan.getGoal());
        res.setPlanDetail(plan.getPlanDetail());
        res.setMotivationReason(plan.getMotivationReason());
        res.setMethod(plan.getMethod());
        res.setStatus(plan.getStatus());
        res.setCreatedAt(plan.getCreatedAt());
        return res;
    }

    // Hàm sinh daily plan cho template
    private String generateTemplatePlanDetail(int startCigarettesPerDay, int totalDays, String templateType) {
        List<Map<String, Object>> plan = new ArrayList<>();
        int remaining = startCigarettesPerDay;
        int step = Math.max(1, startCigarettesPerDay / totalDays);

        String note;
        switch (templateType.toUpperCase()) {
            case "LIGHT":  note = "Giảm nhẹ, vận động nhẹ nhàng."; break;
            case "MEDIUM": note = "Tập trung thể thao và nước ép hoa quả."; break;
            case "HEAVY":  note = "Chú ý stress, tìm người đồng hành."; break;
            case "COACH":  note = "Theo sát lộ trình coach, tương tác mỗi ngày."; break;
            default:       note = "Chúc bạn vững vàng mỗi ngày."; break;
        }

        for (int day = 1; day <= totalDays; day++) {
            Map<String, Object> dayTask = new HashMap<>();
            dayTask.put("day", day);

            if (day < totalDays) {
                remaining = Math.max(0, remaining - step);
                dayTask.put("cigarettes", remaining);
                dayTask.put("note", note);
            } else {
                dayTask.put("cigarettes", 0);
                dayTask.put("note", "Chúc mừng, bạn đã bỏ thuốc!");
            }
            plan.add(dayTask);
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(plan);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi sinh plan detail", e);
        }
    }
    public List<QuitPlanResponse> getHistoryPlans(String username) {
        Account account = getAccountByUsername(username);
        List<QuitPlan> plans = quitPlanRepository.findAllByAccountOrderByCreatedAtDesc(account);
        return plans.stream().map(this::mapToResponse).toList();
    }

    public QuitPlanResponse getQuitPlanDetail(String username, Long planId) {
        Account account = getAccountByUsername(username);
        QuitPlan plan = quitPlanRepository.findByIdAndAccount(planId, account)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kế hoạch"));
        return mapToResponse(plan);
    }

}
