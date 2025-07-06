package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.PurchasedPlanRequest;
import com.quitsmoking.platform.dto.PurchasedPlanResponse;
import com.quitsmoking.platform.service.PurchasedPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchased-plan")
@SecurityRequirement(name = "api")
@Tag(name = "PurchasedPlan")
public class PurchasedPlanAPI {

    @Autowired
    private PurchasedPlanService purchasedPlanService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/buy")
    public ResponseEntity<PurchasedPlanResponse> buyPlan(Authentication auth,
                                                         @RequestBody PurchasedPlanRequest request) {
        PurchasedPlanResponse response = purchasedPlanService.buyPlan(auth.getName(), request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/{planId}/activate")
    public ResponseEntity<PurchasedPlanResponse> activatePlan(Authentication auth,
                                                              @PathVariable Long planId) {
        PurchasedPlanResponse response = purchasedPlanService.activatePurchasedPlan(planId, auth.getName());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my")
    public ResponseEntity<List<PurchasedPlanResponse>> getMyPlans(Authentication auth) {
        List<PurchasedPlanResponse> response = purchasedPlanService.getUserPurchasedPlans(auth.getName());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/active")
    public ResponseEntity<PurchasedPlanResponse> getActivePlan(Authentication auth) {
        PurchasedPlanResponse response = purchasedPlanService.getActivePlan(auth.getName());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/my/{id}")
    public ResponseEntity<PurchasedPlanResponse> getMyPurchasedPlanById(
            Authentication auth,
            @PathVariable Long id
    ) {
        PurchasedPlanResponse response = purchasedPlanService.getUserPurchasedPlanById(auth.getName(), id);
        return ResponseEntity.ok(response);
    }

}
