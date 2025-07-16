package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.DailySuccessAmountResponse;
import com.quitsmoking.platform.dto.PackageRevenueResponse;
import com.quitsmoking.platform.dto.TotalSuccessAmountResponse;
import com.quitsmoking.platform.dto.DashboardUserStatsResponse;
import com.quitsmoking.platform.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Dashboard")
public class AdminDashboardAPI {

    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "Tổng tiền giao dịch thành công theo thời gian")
    @GetMapping("/total-success-amount")
    public ResponseEntity<TotalSuccessAmountResponse> getTotalSuccessAmount(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        TotalSuccessAmountResponse response = dashboardService.getTotalSuccessAmount(from, to);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Biểu đồ tổng tiền giao dịch thành công theo ngày")
    @GetMapping("/daily-success-amount")
    public ResponseEntity<DailySuccessAmountResponse> getDailySuccessAmount(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        DailySuccessAmountResponse response = dashboardService.getDailySuccessAmount(from, to);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Biểu đồ doanh thu theo từng gói (bao gồm cả gói chưa có giao dịch)")
    @GetMapping("/package-revenue")
    public ResponseEntity<List<PackageRevenueResponse>> getPackageRevenue(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<PackageRevenueResponse> response = dashboardService.getRevenueByPackage(from, to);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Tổng quan số lượng user")
    @GetMapping("/user-stats")
    public ResponseEntity<DashboardUserStatsResponse> getUserStats() {
        long totalUser = dashboardService.getTotalUserCount();
        long activeUser = dashboardService.getTotalActiveUser();
        long userHasPurchasedPlan = dashboardService.getTotalUserHasPurchasedPlan();
        DashboardUserStatsResponse res = new DashboardUserStatsResponse(totalUser, activeUser, userHasPurchasedPlan);
        return ResponseEntity.ok(res);
    }


}
