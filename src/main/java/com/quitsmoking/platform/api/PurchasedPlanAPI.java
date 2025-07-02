package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.PurchaseRequest;
import com.quitsmoking.platform.dto.PurchasedPlanResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.service.PurchasedPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchased-plan")
@SecurityRequirement(name = "api")
@Tag(name = "PurchasedPlan")
public class PurchasedPlanAPI {
    @Autowired
    private PurchasedPlanService purchasedPlanService;

    @PostMapping("/purchase")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PurchasedPlan> purchase(@AuthenticationPrincipal Account account,
                                                  @RequestBody PurchaseRequest request) {
        PurchasedPlan result = purchasedPlanService.createPurchasedPlan(account.getUsername(),
                request.getAmount());
        return ResponseEntity.ok(result);
    }

    // List all unused purchased plans of current user
    @GetMapping("/unused")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<PurchasedPlanResponse>> getUnused(@AuthenticationPrincipal Account account) {
        List<PurchasedPlan> plans = purchasedPlanService.getUnusedPlans(account.getUsername());
        List<PurchasedPlanResponse> res = plans.stream().map(p -> {
            PurchasedPlanResponse r = new PurchasedPlanResponse();
            r.setId(p.getId());
            r.setTemplateType(p.getTemplateType());
            return r;
        }).toList();
        return ResponseEntity.ok(res);
    }
}
