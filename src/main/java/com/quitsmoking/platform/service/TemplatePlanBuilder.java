package com.quitsmoking.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.enums.AddictionLevel;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

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
        plan.setMethod(MethodType.PLAN_SAMPLE);

        int expectedDays = Optional.ofNullable(purchasedPlan.getPlanPackage().getDuration()).orElse(30);
        plan.setTargetQuitDate(plan.getStartDate().plusDays(expectedDays - 1));

        plan.setPlanDetail(
                generateTemplatePlanDetail(
                        Math.max(1, ic.getCigarettesPerDay()),
                        expectedDays,
                        purchasedPlan.getPlanPackage().getDescription(),
                        ic.getAddictionLevel() != null ? ic.getAddictionLevel() : AddictionLevel.MODERATE // fallback
                )
        );

        plan.setStatus(PlanStatus.ACTIVE);
        plan.setCreatedAt(LocalDate.now());

        return plan;
    }

    private String generateTemplatePlanDetail(int startCigarettesPerDay, int totalDays, String noteDescription, AddictionLevel addictionLevel) {
        List<Map<String, Object>> planDetails = new ArrayList<>();

        double[] floatPlan = new double[totalDays];
        int[] roundedPlan = new int[totalDays];

        int target = 0;

        // Xác định tỷ lệ giảm chậm tùy theo mức độ nghiện
        double slowPhaseRatio = switch (addictionLevel) {
            case LIGHT -> 0.0;
            case MODERATE -> (totalDays >= 21 ? 0.3 : 0.2);
            case SEVERE -> (totalDays >= 21 ? 0.5 : 0.3);
        };

        int slowDays = (int) Math.round(totalDays * slowPhaseRatio);
        int fastDays = totalDays - slowDays;

        double slowDrop = 0.3;
        double current = startCigarettesPerDay;

        // Giai đoạn chậm: giảm ít mỗi ngày
        for (int i = 0; i < slowDays; i++) {
            current = Math.max(target, current - slowDrop);
            floatPlan[i] = current;
        }

        // Giai đoạn nhanh: chia đều phần còn lại để giảm
        double fastStep = (current - target) / Math.max(1, fastDays);
        for (int i = slowDays; i < totalDays; i++) {
            current = Math.max(target, current - fastStep);
            floatPlan[i] = current;
        }

        // Làm tròn xuống từng ngày & đảm bảo giảm dần không tăng lại
        int lastValue = startCigarettesPerDay + 1;
        for (int i = 0; i < totalDays; i++) {
            int value = (int) Math.floor(floatPlan[i]);
            value = Math.min(value, lastValue);
            value = Math.max(0, value);
            roundedPlan[i] = value;
            lastValue = value;
        }

        // Ép ngày cuối là 0
        roundedPlan[totalDays - 1] = 0;

        // Tạo chi tiết từng ngày dưới dạng JSON
        for (int day = 1; day <= totalDays; day++) {
            Map<String, Object> dayTask = new HashMap<>();
            dayTask.put("day", day);
            dayTask.put("cigarettes", roundedPlan[day - 1]);
            dayTask.put("note", noteDescription);
            planDetails.add(dayTask);
        }

        try {
            return objectMapper.writeValueAsString(planDetails);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi sinh plan detail", e);
        }
    }
}
