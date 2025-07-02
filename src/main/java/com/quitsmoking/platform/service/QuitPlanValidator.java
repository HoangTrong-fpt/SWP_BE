package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
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

        // 1. Kiểm tra đã có điều kiện ban đầu
        if (initialCondition == null) {
            throw new IllegalRequestException("Bạn chưa khai báo điều kiện ban đầu!");
        }

        // 2. Kiểm tra gói mua tồn tại và chưa được dùng
        if (purchasedPlan == null || Boolean.TRUE.equals(purchasedPlan.getUsed())) {
            throw new IllegalRequestException("Gói đã được sử dụng hoặc không tồn tại.");
        }

        // 3. Coach flow validation
        if (purchasedPlan.getTemplateType() == PurchasedTemplateType.COACH && !coachFlow) {
            throw new ForbiddenException("Chỉ huấn luyện viên mới được tạo kế hoạch COACH.");
        }

        // 4. Custom plan validation
        if (request.getMethod() == MethodType.CUSTOM) {
            validateCustomPlan(request, purchasedPlan, coachFlow);
        }

        // 5. Template plan validation
        if (request.getMethod() == MethodType.TEMPLATE) {
            validateTemplatePlan(purchasedPlan);
        }
    }

    private void validateCustomPlan(QuitPlanRequest request, PurchasedPlan purchasedPlan, boolean coachFlow) {
        // Custom plan yêu cầu người dùng hoặc coach cung cấp plan detail
        if (request.getPlanDetail() == null || request.getPlanDetail().isBlank()) {
            throw new IllegalRequestException("Bạn phải cung cấp chi tiết kế hoạch.");
        }

        // Người dùng không thể tự nhập ngày nếu là gói COACH mà không phải coachFlow
        if (purchasedPlan.getTemplateType() == PurchasedTemplateType.COACH && !coachFlow) {
            throw new ForbiddenException("Ngày bắt đầu và kết thúc kế hoạch COACH do huấn luyện viên quyết định.");
        }

        // Người dùng bắt buộc phải nhập ngày nếu không phải gói COACH
        if (purchasedPlan.getTemplateType() != PurchasedTemplateType.COACH &&
                (request.getStartDate() == null || request.getTargetQuitDate() == null)) {
            throw new IllegalRequestException("Ngày bắt đầu và kết thúc không được để trống.");
        }

        // Kiểm tra logic ngày bắt đầu và ngày kết thúc
        if (request.getStartDate() != null && request.getTargetQuitDate() != null &&
                request.getTargetQuitDate().isBefore(request.getStartDate())) {
            throw new IllegalRequestException("Ngày kết thúc phải sau ngày bắt đầu.");
        }
    }

    private void validateTemplatePlan(PurchasedPlan purchasedPlan) {
        // Gói FREE không được phép tạo kế hoạch template
        if (purchasedPlan.getTemplateType() == PurchasedTemplateType.FREE) {
            throw new ForbiddenException("Gói miễn phí không hỗ trợ kế hoạch mẫu.");
        }
    }
}
