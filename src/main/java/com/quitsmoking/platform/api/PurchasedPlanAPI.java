package com.quitsmoking.platform.api;


import com.quitsmoking.platform.dto.PurchasedPlanRequest;
import com.quitsmoking.platform.dto.PurchasedPlanResponse;
import com.quitsmoking.platform.service.PurchasedPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("/buy")
    public ResponseEntity<PurchasedPlanResponse> buyPlan(
            @RequestBody PurchasedPlanRequest req,
            Authentication auth
    ) {
        PurchasedPlanResponse plan = purchasedPlanService.buyPlan(auth.getName(), req);
        return ResponseEntity.ok(plan);
    }


    @PostMapping("/activate/{id}")
    public ResponseEntity<PurchasedPlanResponse> activatePlan(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(purchasedPlanService.activatePurchasedPlan(id, auth.getName()));
    }

    @GetMapping("/my-plans")
    public ResponseEntity<List<PurchasedPlanResponse>> getMyPlans(Authentication auth) {
        return ResponseEntity.ok(purchasedPlanService.getUserPurchasedPlans(auth.getName()));
    }
}