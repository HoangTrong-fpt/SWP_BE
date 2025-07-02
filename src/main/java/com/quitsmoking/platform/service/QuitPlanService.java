package com.quitsmoking.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import com.quitsmoking.platform.exception.exceptions.NotFoundException;
import com.quitsmoking.platform.repository.*;
import com.quitsmoking.platform.service.PurchasedPlanService;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
import com.quitsmoking.platform.exception.exceptions.ResourceNotFoundException;
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

    @Autowired
    private PurchasedPlanService purchasedPlanService;
    @Autowired
    private CoachRepository coachRepository;

    // Use Spring Boot's configured ObjectMapper so Java time types are handled
    @Autowired
    private ObjectMapper objectMapper;

    private Account getAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Activate a purchased plan. Both TEMPLATE and CUSTOM methods now require
    // an unused PurchasedPlan. A template plan cannot be created using a FREE
    // purchased plan.
    @Transactional
    public QuitPlanResponse createQuitPlan(String username, QuitPlanRequest request) {
        if (!purchasedPlanService.hasUnusedOrActivePlan(username)) {
            throw new ForbiddenException("Bạn cần mua gói để sử dụng tính năng này");
        }

        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

        if (quitPlanRepository.existsByAccountAndStatus(account, PlanStatus.ACTIVE)) {
            throw new IllegalStateException("Bạn đã có kế hoạch hoạt động!");
        }

        InitialCondition ic = initialConditionRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("Bạn chưa khai báo điều kiện ban đầu!"));


        QuitPlan plan = new QuitPlan();
        plan.setAccount(account);
        plan.setInitialConditionId(ic.getId());
        try {
            // Serialize using the injected ObjectMapper which is configured
            // with JavaTimeModule by Spring Boot
            plan.setInitialConditionSnapshot(objectMapper.writeValueAsString(ic));
        } catch (Exception e) {

            throw new RuntimeException("Failed to snapshot initial condition", e);

//            throw new RuntimeException("Không thể lưu initial condition snapshot", e);

        }
        plan.setStartDate(request.getStartDate());
        plan.setGoal(request.getGoal());
        plan.setMotivationReason(request.getMotivationReason());
        plan.setCreatedAt(LocalDate.now());

        PurchasedPlan purchasedPlan;

        if (request.getPurchasedPlanId() == null) {
            throw new IllegalStateException("Bạn cần mua gói trước khi tạo kế hoạch");
        }

        purchasedPlan = purchasedPlanRepository
                .findByIdAndAccountAndUsedFalse(request.getPurchasedPlanId(), account)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy gói đã mua hoặc đã sử dụng"));

        if (purchasedPlan.getUsed()) {
            throw new IllegalStateException("Gói đã được sử dụng");
        }

        // Ensure a coach is assigned for COACH type plans before activation
        if (purchasedPlan.getTemplateType() == PurchasedTemplateType.COACH && purchasedPlan.getCoach() == null) {
            throw new IllegalStateException("Gói COACH chưa được gán huấn luyện viên");
        }

        if (request.getMethod() == MethodType.TEMPLATE) {
            if (purchasedPlan.getTemplateType().name().equalsIgnoreCase("FREE")) {
                throw new IllegalStateException("Gói FREE không được phép tạo kế hoạch mẫu (template)");
            }

            String templateType = getIntensity(purchasedPlan.getTemplateType());

            int expectedDays = switch (purchasedPlan.getTemplateType()) {
                case LIGHT -> 30;
                case MEDIUM -> 60;
                case HEAVY -> 90;
                default -> (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getTargetQuitDate()) + 1;
            };

            plan.setMethod(MethodType.TEMPLATE);

            if (purchasedPlan.getTemplateType() == PurchasedTemplateType.COACH) {
                plan.setTargetQuitDate(request.getTargetQuitDate());
                plan.setPlanDetail(generateTemplatePlanDetail(ic.getCigarettesPerDay(), expectedDays, templateType));
            } else {
                plan.setTargetQuitDate(request.getStartDate().plusDays(expectedDays - 1));
                plan.setPlanDetail(generateTemplatePlanDetail(ic.getCigarettesPerDay(), expectedDays, templateType));
            }

        } else if (request.getMethod() == MethodType.CUSTOM) {
            plan.setMethod(MethodType.CUSTOM);
            plan.setTargetQuitDate(request.getTargetQuitDate());
            plan.setPlanDetail(request.getPlanDetail());
        } else {
            throw new IllegalArgumentException("Method không hợp lệ");
        }

        // store template type for reference
        plan.setTemplateType(purchasedPlan.getTemplateType());

        plan.setStatus(PlanStatus.ACTIVE);
        plan.setPurchasedPlan(purchasedPlan);
        QuitPlan saved = quitPlanRepository.save(plan);

        if (purchasedPlan != null) {
            purchasedPlan.setUsed(true);
            purchasedPlan.setLinkedQuitPlan(saved);
            purchasedPlanRepository.save(purchasedPlan);
        }

        return mapToResponse(saved);
    }


    public QuitPlanResponse getActiveQuitPlan(String username) {
        if (!purchasedPlanService.hasUnusedOrActivePlan(username)) {
            throw new ForbiddenException("Bạn cần mua gói để sử dụng tính năng này");
        }

        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

        QuitPlan plan = quitPlanRepository.findByAccountAndStatus(account, PlanStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("Không có kế hoạch hoạt động"));
        return mapToResponse(plan);
    }

    @Transactional
    public void cancelQuitPlan(String username, Long id) {
        if (!purchasedPlanService.hasUnusedOrActivePlan(username)) {
            throw new ForbiddenException("Bạn cần mua gói để sử dụng tính năng này");
        }

        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));
        QuitPlan plan = quitPlanRepository.findByIdAndAccount(id, account)
                .orElseThrow(() -> new IllegalArgumentException("ID kế hoạch không hợp lệ"));
        plan.setStatus(PlanStatus.CANCELLED);
        quitPlanRepository.save(plan);
    }

    /**
     * Map {@link PurchasedTemplateType} to plan intensity string used when
     * generating template plan details. FREE is treated as LIGHT but normally
     * cannot be used for template plans.
     */
    private String getIntensity(PurchasedTemplateType type) {
        return switch (type) {
            case FREE -> "LIGHT";
            case LIGHT -> "LIGHT";
            case MEDIUM -> "MEDIUM";
            case HEAVY -> "HEAVY";
            case COACH -> "COACH";
        };
    }

    private QuitPlanResponse mapToResponse(QuitPlan plan) {
        QuitPlanResponse res = new QuitPlanResponse();
        res.setId(plan.getId());
        res.setInitialConditionId(plan.getInitialConditionId());

        res.setInitialConditionSnapshot(plan.getInitialConditionSnapshot());

        res.setStartDate(plan.getStartDate());
        res.setTargetQuitDate(plan.getTargetQuitDate());
        res.setGoal(plan.getGoal());
        res.setPlanDetail(plan.getPlanDetail());
        res.setMotivationReason(plan.getMotivationReason());
        res.setMethod(plan.getMethod());
        if (plan.getPurchasedPlan() != null) {
            res.setPurchasedPlanId(plan.getPurchasedPlan().getId());
        }
        res.setTemplateType(plan.getTemplateType());
        res.setStatus(plan.getStatus());
        res.setCreatedAt(plan.getCreatedAt());
        return res;
    }

    // Hàm sinh daily plan cho template
    private String generateTemplatePlanDetail(int startCigarettesPerDay, int totalDays, String templateType) {
        List<Map<String, Object>> plan = new ArrayList<>();
        int remaining = startCigarettesPerDay;

        int baseStep = Math.max(1, startCigarettesPerDay / totalDays);
        int step;
        switch (templateType.toUpperCase()) {
            case "LIGHT":
                // slow reduction
                step = Math.max(1, baseStep / 2);
                break;
            case "MEDIUM":
                step = baseStep;
                break;
            case "HEAVY":
                // faster reduction
                step = Math.max(1, (int) Math.ceil(baseStep * 1.5));
                break;
            case "COACH":
                step = baseStep;
                break;
            default:
                step = baseStep;
                break;
        }

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
                if ("COACH".equalsIgnoreCase(templateType)) {
                    dayTask.put("coachCheckIn", true);
                    dayTask.put("task", "Bài tập coach ngày " + day);
                }
            } else {
                dayTask.put("cigarettes", 0);
                dayTask.put("note", "Chúc mừng, bạn đã bỏ thuốc!");
                if ("COACH".equalsIgnoreCase(templateType)) {
                    dayTask.put("coachCheckIn", true);
                    dayTask.put("task", "Tổng kết với coach");
                }
            }
            plan.add(dayTask);
        }
        try {
            // Reuse the configured ObjectMapper for consistency
            return objectMapper.writeValueAsString(plan);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi sinh plan detail", e);
        }
    }
    public List<QuitPlanResponse> getHistoryPlans(String username) {
        if (!purchasedPlanService.hasUnusedOrActivePlan(username)) {
            throw new ForbiddenException("Bạn cần mua gói để sử dụng tính năng này");
        }

        Account account = getAccountByUsername(username);
        List<QuitPlan> plans = quitPlanRepository.findAllByAccountOrderByCreatedAtDesc(account);
        return plans.stream().map(this::mapToResponse).toList();
    }

    public QuitPlanResponse getQuitPlanDetail(String username, Long planId) {
        if (!purchasedPlanService.hasUnusedOrActivePlan(username)) {
            throw new ForbiddenException("Bạn cần mua gói để sử dụng tính năng này");
        }

        Account account = getAccountByUsername(username);
        QuitPlan plan = quitPlanRepository.findByIdAndAccount(planId, account)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kế hoạch"));
        return mapToResponse(plan);
    }

    @Transactional
    public QuitPlanResponse createPlanForClient(String coachUsername, String clientUsername, QuitPlanRequest request) {
        Account coach = accountRepository.findAccountByUsername(coachUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Coach not found"));
        if (coach.getRole() != com.quitsmoking.platform.enums.Role.COACH) {
            throw new ForbiddenException("Không phải tài khoản huấn luyện viên");
        }

        Account client = getAccountByUsername(clientUsername);

        PurchasedPlan plan = purchasedPlanRepository.findByIdAndAccountAndUsedFalse(request.getPurchasedPlanId(), client)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy gói đã mua hoặc đã sử dụng"));

        if (plan.getTemplateType() != PurchasedTemplateType.COACH) {
            throw new IllegalStateException("Gói này không phải loại COACH");
        }

        Coach assignedCoach = coachRepository.findByAccountUsername(coachUsername)
                .orElseThrow(() -> new NotFoundException("Coach with username " + coachUsername + " not found"));

        plan.setCoach(assignedCoach);

        return createQuitPlan(clientUsername, request);
    }

}
