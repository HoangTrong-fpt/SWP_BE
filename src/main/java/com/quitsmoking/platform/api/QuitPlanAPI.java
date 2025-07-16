package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.service.QuitPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quit-plan")
@SecurityRequirement(name = "api")
//@CrossOrigin("*")
@Tag(name = "QuitPlan")
public class QuitPlanAPI {

    @Autowired
    private QuitPlanService quitPlanService;


    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/active")
    public ResponseEntity<QuitPlanResponse> getActiveQuitPlan(Authentication auth) {
        QuitPlanResponse response = quitPlanService.getActiveQuitPlan(auth);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/history")
    public ResponseEntity<List<QuitPlanResponse>> getHistory(Authentication auth) {
        List<QuitPlanResponse> response = quitPlanService.getHistoryPlans(auth);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelQuitPlan(Authentication auth, @PathVariable Long id) {
        quitPlanService.cancelQuitPlan(auth, id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/detail/{id}")
    public ResponseEntity<QuitPlanResponse> getQuitPlanDetail(
            Authentication auth,
            @PathVariable Long id
    ) {
        QuitPlanResponse response = quitPlanService.getQuitPlanDetail(auth, id);
        return ResponseEntity.ok(response);
    }

}