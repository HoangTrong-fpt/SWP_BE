package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.HealthStatResponse;
import com.quitsmoking.platform.dto.SmokingLogRequest;
import com.quitsmoking.platform.dto.SmokingStatsSummary;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Coach;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import com.quitsmoking.platform.service.SmokingLogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@SecurityRequirement(name = "api")
@RequestMapping("/api/smoking-log")
public class SmokingLogAPI {

    @Autowired
    private SmokingLogService smokingLogService;
    @Autowired
    private AuthenticationRepository accountRepo;
    @Autowired
    private PurchasedPlanRepository purchasedPlanRepo;
    // Ghi nhận log cho ngày hôm nay
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ResponseEntity<HealthStatResponse> recordSmokingLog(
            @RequestBody SmokingLogRequest req,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Account requester = accountRepo.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        HealthStatResponse response = smokingLogService.recordSmokingLog(requester, req);
        return ResponseEntity.ok(response);
    }

    // Lấy log và chỉ số 1 ngày bất kỳ cho user hiện tại
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'COACH')")
    @GetMapping("/day")
    public ResponseEntity<HealthStatResponse> getHealthStatOfDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Account requester = accountRepo.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        HealthStatResponse response = smokingLogService.getHealthStatOfDay(requester, date);
        return ResponseEntity.ok(response);
    }

    // Lấy thống kê tổng hợp cho user hiện tại
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'COACH')")
    @GetMapping("/stats")
    public ResponseEntity<SmokingStatsSummary> getStats(
            Authentication authentication
    ) {
        String username = authentication.getName();
        Account requester = accountRepo.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        SmokingStatsSummary summary = smokingLogService.getStats(requester);
        return ResponseEntity.ok(summary);
    }

    // ADMIN/COACH xem log 1 ngày của user khác
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    @GetMapping("/user/{userId}/day")
    public ResponseEntity<HealthStatResponse> getHealthStatOfDayByUser(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication
    ) {
        String requesterUsername = authentication.getName();
        Account requester = accountRepo.findAccountByUsername(requesterUsername)
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        Account targetUser = accountRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

        if (canAccessUserLogs(requester, targetUser)) {
            HealthStatResponse response = smokingLogService.getHealthStatOfDay(targetUser, date);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    //COACH xem stats tổng hợp của user khác
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<SmokingStatsSummary> getStatsByUser(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        String requesterUsername = authentication.getName();
        Account requester = accountRepo.findAccountByUsername(requesterUsername)
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        Account targetUser = accountRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

        if (canAccessUserLogs(requester, targetUser)) {
            SmokingStatsSummary summary = smokingLogService.getStats(targetUser);
            return ResponseEntity.ok(summary);
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    // Hàm kiểm tra quyền COACH/ADMIN
    private boolean canAccessUserLogs(Account requester, Account targetUser) {
        if (requester.getRole() == Role.ADMIN) return true;

        if (requester.getRole() == Role.COACH) {
            // Tìm plan đang active của targetUser
            Optional<PurchasedPlan> activePlanOpt = purchasedPlanRepo.findFirstByAccountAndStatus(targetUser, PlanStatus.ACTIVE);
            if (activePlanOpt.isPresent()) {
                PurchasedPlan plan = activePlanOpt.get();
                Coach coach = plan.getCoach();
                // Giả sử Coach có Account coachAccount
                if (coach != null && coach.getAccount().getId().equals(requester.getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}
