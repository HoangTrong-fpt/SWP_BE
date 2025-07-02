package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.entity.QuitPlan;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
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

        // Thông tin tài khoản & gói mua liên kết
        plan.setAccount(account);
        plan.setPurchasedPlan(purchasedPlan);

        // Thời gian kế hoạch do người dùng tự chọn hoặc do coach quyết định
        plan.setStartDate(resolveStartDate(purchasedPlan, request));
        plan.setTargetQuitDate(resolveTargetQuitDate(purchasedPlan, request));

        // Chi tiết kế hoạch từ người dùng nhập
        plan.setPlanDetail(request.getPlanDetail());

        // Lý do, mục tiêu, snapshot điều kiện ban đầu
        plan.setGoal(request.getGoal());
        plan.setMotivationReason(request.getMotivationReason());
        plan.setInitialConditionSnapshot(initialConditionSnapshot);
        plan.setInitialConditionId(ic.getId());

        // Thông tin kế hoạch
        plan.setMethod(MethodType.CUSTOM);
        plan.setTemplateType(purchasedPlan.getTemplateType());
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setCreatedAt(LocalDate.now());

        return plan;
    }

    // Logic xử lý ngày bắt đầu
    private LocalDate resolveStartDate(PurchasedPlan purchasedPlan, QuitPlanRequest request) {
        if (purchasedPlan.getTemplateType() == PurchasedTemplateType.COACH) {
            // COACH quyết định ngày bắt đầu
            return purchasedPlan.getActivationDate();
        }
        // Người dùng tự chọn ngày bắt đầu
        return request.getStartDate();
    }

    // Logic xử lý ngày kết thúc
    private LocalDate resolveTargetQuitDate(PurchasedPlan purchasedPlan, QuitPlanRequest request) {
        if (purchasedPlan.getTemplateType() == PurchasedTemplateType.COACH) {
            // COACH quyết định ngày kết thúc dựa vào coach set (tạm thời lấy từ request)
            return request.getTargetQuitDate();
        }
        // Người dùng tự chọn ngày kết thúc
        return request.getTargetQuitDate();
    }
}