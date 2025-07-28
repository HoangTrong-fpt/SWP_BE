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

        if (request != null && request.getMethod() != MethodType.PLAN_SAMPLE) {
            throw new IllegalRequestException("Chỉ hỗ trợ kế hoạch mẫu (PLAN_SAMPLE).");
        }

        validateTemplatePlan(purchasedPlan);
    }

    private void validateTemplatePlan(PurchasedPlan purchasedPlan) {
        if (Boolean.TRUE.equals(purchasedPlan.getPlanPackage().getCoachSupport())) {
            throw new ForbiddenException("Gói có huấn luyện viên không sử dụng kế hoạch mẫu.");
        }
    }
}
