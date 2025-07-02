package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.service.QuitPlanService;
import com.quitsmoking.platform.service.PurchasedPlanService;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quit-plan")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
@Tag(name = "QuitPlan")
public class QuitPlanAPI {

    @Autowired
    private QuitPlanService quitPlanService;

    @Autowired
    private PurchasedPlanService purchasedPlanService;

    @PostMapping("/activate")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<QuitPlanResponse> activatePlan(
            @RequestBody @Valid QuitPlanRequest request,
            @AuthenticationPrincipal Account account
    ) {
        if (!purchasedPlanService.hasUnusedOrActivePlan(account.getUsername())) {
            throw new ForbiddenException("Bạn cần mua gói để sử dụng tính năng này");
        }
        QuitPlanResponse response = quitPlanService.createQuitPlan(account.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<QuitPlanResponse> getActiveQuitPlan(
            @AuthenticationPrincipal Account account
    ) {
        if (!purchasedPlanService.hasUnusedOrActivePlan(account.getUsername())) {
            throw new ForbiddenException("Bạn cần mua gói để sử dụng tính năng này");
        }
        QuitPlanResponse response = quitPlanService.getActiveQuitPlan(account.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> cancelQuitPlan(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account
    ) {
        if (!purchasedPlanService.hasUnusedOrActivePlan(account.getUsername())) {
            throw new ForbiddenException("Bạn cần mua gói để sử dụng tính năng này");
        }
        quitPlanService.cancelQuitPlan(account.getUsername(), id);
        return ResponseEntity.ok("Quit plan cancelled successfully");
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<QuitPlanResponse>> getHistory(@AuthenticationPrincipal Account account) {
        if (!purchasedPlanService.hasUnusedOrActivePlan(account.getUsername())) {
            throw new ForbiddenException("Bạn cần mua gói để sử dụng tính năng này");
        }
        List<QuitPlanResponse> plans = quitPlanService.getHistoryPlans(account.getUsername());
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<QuitPlanResponse> getPlanDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account
    ) {
        if (!purchasedPlanService.hasUnusedOrActivePlan(account.getUsername())) {
            throw new ForbiddenException("Bạn cần mua gói để sử dụng tính năng này");
        }
        QuitPlanResponse response = quitPlanService.getQuitPlanDetail(account.getUsername(), id);
        return ResponseEntity.ok(response);
    }

}
