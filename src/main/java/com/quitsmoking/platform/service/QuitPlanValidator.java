package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import com.quitsmoking.platform.exception.exceptions.IllegalRequestException;
import org.springframework.stereotype.Component;

@Component
public class QuitPlanValidator {

    public void validate(Account account,
                         PurchasedPlan purchasedPlan,
                         QuitPlanRequest request,
                         InitialCondition initialCondition,
                         boolean coachFlow) {

        if (initialCondition == null) {
            throw new IllegalRequestException("Bạn chưa khai báo điều kiện ban đầu!");
        }

        if (purchasedPlan == null || Boolean.TRUE.equals(purchasedPlan.getUsed())) {
            throw new IllegalRequestException("Gói đã được sử dụng hoặc không tồn tại.");
        }

        if (isCoachPlan(purchasedPlan) && !coachFlow) {
            throw new ForbiddenException("Chỉ huấn luyện viên mới được tạo kế hoạch COACH.");
        }

        if (request != null && request.getMethod() == MethodType.PLAN_COACH) {
            validateCustomPlan(request, purchasedPlan, coachFlow);
        }

        if (request != null && request.getMethod() == MethodType.PLAN_SAMPLE) {
            validateTemplatePlan(purchasedPlan);
        }
    }

    private void validateCustomPlan(QuitPlanRequest request, PurchasedPlan purchasedPlan, boolean coachFlow) {
        if (request.getPlanDetail() == null || request.getPlanDetail().isBlank()) {
            throw new IllegalRequestException("Bạn phải cung cấp chi tiết kế hoạch.");
        }

        if (isCoachPlan(purchasedPlan) && !coachFlow) {
            throw new ForbiddenException("Ngày bắt đầu và kết thúc kế hoạch COACH do huấn luyện viên quyết định.");
        }

        if (!isCoachPlan(purchasedPlan) &&
                (request.getStartDate() == null || request.getTargetQuitDate() == null)) {
            throw new IllegalRequestException("Ngày bắt đầu và kết thúc không được để trống.");
        }

        if (request.getStartDate() != null && request.getTargetQuitDate() != null &&
                request.getTargetQuitDate().isBefore(request.getStartDate())) {
            throw new IllegalRequestException("Ngày kết thúc phải sau ngày bắt đầu.");
        }
    }

    private void validateTemplatePlan(PurchasedPlan purchasedPlan) {
        if (Boolean.TRUE.equals(purchasedPlan.getPlanPackage().getCoachSupport())) {
            throw new ForbiddenException("Gói có huấn luyện viên không sử dụng kế hoạch mẫu.");
        }
    }

    private boolean isCoachPlan(PurchasedPlan purchasedPlan) {
        return Boolean.TRUE.equals(purchasedPlan.getPlanPackage().getCoachSupport());
    }
}
