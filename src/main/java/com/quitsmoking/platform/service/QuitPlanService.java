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
import com.quitsmoking.platform.enums.PurchasedTemplateType;
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

    // Activate a purchased plan. If purchasedPlanId is null, only return a preview
    // of the custom plan without persisting.
    @Transactional
    public QuitPlanResponse createQuitPlan(String username, QuitPlanRequest request) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

        if (quitPlanRepository.existsByAccountAndStatus(account, PlanStatus.ACTIVE)) {
            throw new IllegalStateException("Bạn đã có kế hoạch hoạt động!");
        }

        InitialCondition ic = initialConditionRepository.findByAccountAndIsActiveTrue(account)
                .orElseThrow(() -> new IllegalArgumentException("Bạn chưa khai báo điều kiện ban đầu!"));

        // Preview only if user has no purchased plan id provided
        boolean previewOnly = request.getPurchasedPlanId() == null;

        QuitPlan plan = new QuitPlan();
        plan.setAccount(account);
        plan.setInitialCondition(ic);
        plan.setStartDate(request.getStartDate());
        plan.setTargetQuitDate(request.getTargetQuitDate());
        plan.setGoal(request.getGoal());
        plan.setMotivationReason(request.getMotivationReason());
        plan.setCreatedAt(LocalDate.now());

        PurchasedPlan purchasedPlan = null;

        if (request.getMethod() == MethodType.TEMPLATE) {
            if (previewOnly) {
                throw new IllegalStateException("Bạn cần mua gói trả phí để sử dụng kế hoạch mẫu");
            }
            purchasedPlan = purchasedPlanRepository.findByIdAndAccountAndUsedFalse(request.getPurchasedPlanId(), account)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói đã mua"));
            if (purchasedPlan.getUsed()) {
                throw new IllegalStateException("Gói đã được sử dụng");
            }
            if (purchasedPlan.getTemplateType().name().equalsIgnoreCase("FREE")) {
                throw new IllegalStateException("Gói FREE không được phép tạo kế hoạch mẫu (template)");
            }

            String templateType = getIntensity(purchasedPlan.getTemplateType());
            int totalDays = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getTargetQuitDate()) + 1;

            plan.setMethod(MethodType.TEMPLATE);
            plan.setPlanDetail(generateTemplatePlanDetail(ic.getCigarettesPerDay(), totalDays, templateType));
        } else if (request.getMethod() == MethodType.CUSTOM) {
            plan.setMethod(MethodType.CUSTOM);
            plan.setPlanDetail(request.getPlanDetail());
        } else {
            throw new IllegalArgumentException("Method không hợp lệ");
        }

        if (previewOnly) {
            // For free users just preview the plan
            return mapToResponse(plan);
        }

        plan.setStatus(PlanStatus.ACTIVE);
        plan.setPurchasedPlan(purchasedPlan);
        QuitPlan saved = quitPlanRepository.save(plan);

        purchasedPlan.setUsed(true);
        purchasedPlan.setLinkedQuitPlan(saved);
        purchasedPlanRepository.save(purchasedPlan);

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

    /**
     * Map PurchasedTemplateType to plan intensity string.
     * FREE/TEMPLATE_100K -> LIGHT, TEMPLATE_200K -> MEDIUM,
     * TEMPLATE_300K -> HEAVY, TEMPLATE_500K -> COACH
     */
    private String getIntensity(PurchasedTemplateType type) {
        return switch (type) {
            case FREE, TEMPLATE_100K -> "LIGHT";
            case TEMPLATE_200K -> "MEDIUM";
            case TEMPLATE_300K -> "HEAVY";
            case TEMPLATE_500K -> "COACH";
        };
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
        if (plan.getPurchasedPlan() != null) {
            res.setPurchasedPlanId(plan.getPurchasedPlan().getId());
        }
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
