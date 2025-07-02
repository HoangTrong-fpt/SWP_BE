package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.FreeQuitPlanRequest;
import com.quitsmoking.platform.dto.FreeQuitPlanResponse;
import com.quitsmoking.platform.service.FreeQuitPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/free-plan")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
@Tag(name = "free-plan")
public class FreeQuitPlanAPI {

    @Autowired
    private FreeQuitPlanService freeQuitPlanService;

    @PostMapping("/create")
    public ResponseEntity<FreeQuitPlanResponse> createFreePlan(@RequestBody FreeQuitPlanRequest request, Authentication auth) {
        return ResponseEntity.ok(freeQuitPlanService.createFreePlan(auth.getName(), request));
    }

    @GetMapping("/active")
    public ResponseEntity<FreeQuitPlanResponse> getActivePlan(Authentication auth) {
        return ResponseEntity.ok(freeQuitPlanService.getActiveFreePlan(auth.getName()));
    }

    @PutMapping("/cancel")
    public ResponseEntity<Void> cancelFreePlan(Authentication auth) {
        freeQuitPlanService.cancelFreePlan(auth.getName());
        return ResponseEntity.ok().build();
    }
}