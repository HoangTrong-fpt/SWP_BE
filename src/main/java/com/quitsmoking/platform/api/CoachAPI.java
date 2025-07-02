package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.dto.UserAccountResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.service.CoachService;
import com.quitsmoking.platform.service.QuitPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coach")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
@Tag(name = "Coach")
public class CoachAPI {
    @Autowired
    private CoachService coachService;
    @Autowired
    private QuitPlanService quitPlanService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/plans/{planId}/assign")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<String> assignPlan(@AuthenticationPrincipal Account coach,
                                             @PathVariable Long planId) {
        coachService.assignCoachToPlan(coach.getUsername(), planId);
        return ResponseEntity.ok("Assigned successfully");
    }

    @GetMapping("/clients")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<List<UserAccountResponse>> myClients(@AuthenticationPrincipal Account coach) {
        List<Account> clients = coachService.getClients(coach.getUsername());
        List<UserAccountResponse> res = clients.stream()
                .map(c -> modelMapper.map(c, UserAccountResponse.class))
                .toList();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/clients/{username}/plan")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<QuitPlanResponse> createPlan(@AuthenticationPrincipal Account coach,
                                                       @PathVariable String username,
                                                       @RequestBody @Valid QuitPlanRequest request) {
        QuitPlanResponse res = quitPlanService.createPlanForClient(coach.getUsername(), username, request);
        return ResponseEntity.ok(res);
    }
}
