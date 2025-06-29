package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.PurchaseRequest;
import com.quitsmoking.platform.dto.PurchasedPlanRequest;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.service.PurchasedPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchased-plan")
@SecurityRequirement(name = "api")
@Tag(name = "PurchasedPlan")
public class PurchasedPlanAPI {
    @Autowired
    private PurchasedPlanService purchasedPlanService;

    @PostMapping("/purchase")
    public ResponseEntity<PurchasedPlan> purchase(@AuthenticationPrincipal Account account,
                                                  @RequestBody PurchaseRequest request) {
        PurchasedPlan result = purchasedPlanService.createPurchasedPlan(account.getUsername(),
                request.getTemplateType().name());
        return ResponseEntity.ok(result);
}}
