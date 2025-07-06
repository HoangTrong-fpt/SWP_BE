package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.FreeQuitPlanRequest;
import com.quitsmoking.platform.dto.FreeQuitPlanResponse;
import com.quitsmoking.platform.service.FreeQuitPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/free-plan")
@SecurityRequirement(name = "api")
//@CrossOrigin("*")
@Tag(name = "free-plan")
public class FreeQuitPlanAPI {

    @Autowired
    private FreeQuitPlanService freeQuitPlanService;

    // ✅ Chỉ CUSTOMER được tạo free plan
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/create")
    public ResponseEntity<FreeQuitPlanResponse> createFreePlan(
            Authentication auth,
            @RequestBody FreeQuitPlanRequest request) {

        FreeQuitPlanResponse response = freeQuitPlanService.createFreePlan(auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ Chỉ CUSTOMER được xem kế hoạch miễn phí hiện tại
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/active")
    public ResponseEntity<FreeQuitPlanResponse> getActivePlan(Authentication auth) {

        FreeQuitPlanResponse response = freeQuitPlanService.getActiveFreePlan(auth.getName());
        return ResponseEntity.ok(response);
    }

    // ✅ Chỉ CUSTOMER được hủy free plan
    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/cancel")
    public ResponseEntity<Void> cancelFreePlan(Authentication auth) {

        freeQuitPlanService.cancelFreePlan(auth.getName());
        return ResponseEntity.noContent().build();
    }
}
