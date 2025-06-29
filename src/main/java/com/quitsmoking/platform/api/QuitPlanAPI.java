package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.service.QuitPlanService;
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

    @PostMapping("/activate")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<QuitPlanResponse> activatePlan(
            @RequestBody @Valid QuitPlanRequest request,
            @AuthenticationPrincipal Account account
    ) {
        QuitPlanResponse response = quitPlanService.createQuitPlan(account.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<QuitPlanResponse> getActiveQuitPlan(
            @AuthenticationPrincipal Account account
    ) {
        QuitPlanResponse response = quitPlanService.getActiveQuitPlan(account.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> cancelQuitPlan(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account
    ) {
        quitPlanService.cancelQuitPlan(account.getUsername(), id);
        return ResponseEntity.ok("Quit plan cancelled successfully");
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<QuitPlanResponse>> getHistory(@AuthenticationPrincipal Account account) {
        List<QuitPlanResponse> plans = quitPlanService.getHistoryPlans(account.getUsername());
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<QuitPlanResponse> getPlanDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal Account account
    ) {
        QuitPlanResponse response = quitPlanService.getQuitPlanDetail(account.getUsername(), id);
        return ResponseEntity.ok(response);
    }

}
