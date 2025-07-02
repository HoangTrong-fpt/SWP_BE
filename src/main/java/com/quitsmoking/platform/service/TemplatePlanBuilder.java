package com.quitsmoking.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.entity.QuitPlan;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TemplatePlanBuilder {
    @Autowired
    private ObjectMapper objectMapper;

    public QuitPlan build(Account account, PurchasedPlan purchasedPlan, InitialCondition ic, String goal) {
        QuitPlan plan = new QuitPlan();

        plan.setAccount(account);
        plan.setPurchasedPlan(purchasedPlan);
        plan.setStartDate(purchasedPlan.getActivationDate());
        plan.setGoal(goal);
        plan.setMethod(MethodType.TEMPLATE);

        int expectedDays = switch (purchasedPlan.getTemplateType()) {
            case LIGHT -> 30;
            case MEDIUM -> 60;
            case HEAVY -> 90;
            default -> 30; // Default fallback
        };

        plan.setTargetQuitDate(plan.getStartDate().plusDays(expectedDays - 1));
        plan.setPlanDetail(generateTemplatePlanDetail(ic.getCigarettesPerDay(), expectedDays, purchasedPlan.getTemplateType().name()));

        plan.setTemplateType(purchasedPlan.getTemplateType());
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setCreatedAt(LocalDate.now());

        return plan;
    }

    private String generateTemplatePlanDetail(int startCigarettesPerDay, int totalDays, String templateType) {
        List<Map<String, Object>> planDetails = new ArrayList<>();
        int remaining = startCigarettesPerDay;

        int step = Math.max(1, startCigarettesPerDay / totalDays);
        String note = switch (templateType.toUpperCase()) {
            case "LIGHT" -> "Giảm nhẹ, vận động nhẹ nhàng.";
            case "MEDIUM" -> "Tập trung thể thao và nước ép.";
            case "HEAVY" -> "Chú ý stress, tìm người đồng hành.";
            default -> "Chúc bạn kiên trì mỗi ngày.";
        };

        for (int day = 1; day <= totalDays; day++) {
            Map<String, Object> dayTask = new HashMap<>();
            dayTask.put("day", day);

            remaining = Math.max(0, remaining - step);
            dayTask.put("cigarettes", remaining);
            dayTask.put("note", note);

            planDetails.add(dayTask);
        }

        try {
            return objectMapper.writeValueAsString(planDetails);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi sinh plan detail", e);
        }
    }
}
