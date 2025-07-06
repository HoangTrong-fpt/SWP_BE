package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CustomPlanBuilder {

    public QuitPlan build(Account account,
                          PurchasedPlan purchasedPlan,
                          QuitPlanRequest request,
                          InitialCondition ic,
                          String initialConditionSnapshot) {

        QuitPlan plan = new QuitPlan();

        plan.setAccount(account);
        plan.setPurchasedPlan(purchasedPlan);
        plan.setStartDate(resolveStartDate(purchasedPlan, request));
        plan.setTargetQuitDate(resolveTargetQuitDate(purchasedPlan, request));
        plan.setPlanDetail(request.getPlanDetail());
        plan.setGoal(request.getGoal());
        plan.setMotivationReason(request.getMotivationReason());
        plan.setInitialConditionSnapshot(initialConditionSnapshot);
        plan.setInitialConditionId(ic.getId());
        plan.setMethod(request.getMethod() != null ? request.getMethod() : MethodType.PLAN_COACH);
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setCreatedAt(LocalDate.now());

        return plan;
    }

    private LocalDate resolveStartDate(PurchasedPlan purchasedPlan, QuitPlanRequest request) {
        if (Boolean.TRUE.equals(purchasedPlan.getPlanPackage().getCoachSupport())) {
            return purchasedPlan.getActivationDate();
        }
        return request.getStartDate();
    }

    private LocalDate resolveTargetQuitDate(PurchasedPlan purchasedPlan, QuitPlanRequest request) {
        LocalDate target = request.getTargetQuitDate();
        if (target == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được để trống.");
        }
        return target;
    }
}
