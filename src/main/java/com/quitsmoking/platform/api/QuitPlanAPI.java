package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.service.QuitPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<QuitPlanResponse> activateQuitPlan(@RequestBody QuitPlanRequest request, Authentication auth) {
        QuitPlanResponse response = quitPlanService.createQuitPlan(auth.getName(), request, false);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<QuitPlanResponse> getActivePlan(Authentication auth) {
        QuitPlanResponse response = quitPlanService.getActiveQuitPlan(auth.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/cancel/{planId}")
    public ResponseEntity<Void> cancelPlan(@PathVariable Long planId, Authentication auth) {
        quitPlanService.cancelQuitPlan(auth.getName(), planId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<QuitPlanResponse>> history(Authentication auth) {
        List<QuitPlanResponse> responses = quitPlanService.getHistoryPlans(auth.getName());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<QuitPlanResponse> getDetail(@PathVariable Long planId, Authentication auth) {
        QuitPlanResponse response = quitPlanService.getQuitPlanDetail(auth.getName(), planId);
        return ResponseEntity.ok(response);
    }

}
