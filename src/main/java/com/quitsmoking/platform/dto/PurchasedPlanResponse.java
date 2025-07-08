package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.PaymentStatus;
import com.quitsmoking.platform.enums.PlanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "Thông tin chi tiết của gói dịch vụ mà người dùng đã mua")
public class PurchasedPlanResponse {

    @Schema(description = "ID của bản ghi purchased plan", example = "101")
    private Long id;

    @Schema(description = "ID người dùng đã mua gói", example = "12")
    private Long accountId;

    @Schema(description = "Thông tin gói dịch vụ đã mua")
    private PackageResponse packageInfo;

    @Schema(description = "ID của coach (nếu có)", example = "7", nullable = true)
    private Long coachId;

    @Schema(description = "Thời điểm người dùng thực hiện mua gói", example = "2025-07-05T14:30:00")
    private LocalDateTime purchasedAt;

    @Schema(description = "Ngày kích hoạt gói dịch vụ", example = "2025-07-07", nullable = true)
    private LocalDate activationDate;

    @Schema(description = "Trạng thái thanh toán", example = "SUCCESS")
    private PaymentStatus paymentStatus;

    @Schema(description = "Trạng thái sử dụng gói", example = "ACTIVE")
    private PlanStatus status;

    private String paymentUrl;
}
