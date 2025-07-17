package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.CoachResponse;
import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.service.CoachService;
import com.quitsmoking.platform.service.QuitPlanService;
import com.quitsmoking.platform.service.TemplatePlanBuilder;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.repository.AccountRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coach")
@SecurityRequirement(name = "api")
//@CrossOrigin("*")
@Tag(name = "CoachAPI")
public class CoachAPI {

    @Autowired
    private CoachService coachService;
    @Autowired
    private TemplatePlanBuilder templatePlanBuilder;
    @Autowired
    private QuitPlanService quitPlanService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PurchasedPlanRepository purchasedPlanRepository;

    @PreAuthorize("hasRole('COACH')")
    @PostMapping("/client/{clientUsername}/plan")
    public ResponseEntity<QuitPlanResponse> createPlanForClient(Authentication auth,
                                                                @PathVariable String clientUsername,
                                                                @RequestBody QuitPlanRequest request) {
        QuitPlanResponse response = coachService.createPlanForClient(auth.getName(), clientUsername, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('COACH')")
    @GetMapping("/daily-tips")
    public ResponseEntity<List<String>> getDailyTips() {
        return ResponseEntity.ok(templatePlanBuilder.getDailyTips());
    }

    @PreAuthorize("hasRole('COACH')")
    @PostMapping("/client/{clientId}/plan")
    public ResponseEntity<QuitPlanResponse> createPlanForClient(@PathVariable Long clientId, @RequestBody QuitPlanRequest request) {
        Account customer = accountRepository.findById(clientId).orElseThrow(() -> new RuntimeException("Customer not found"));
        PurchasedPlan purchasedPlan = purchasedPlanRepository.findById(request.getPurchasedPlanId())
            .orElseThrow(() -> new RuntimeException("Plan not found"));
        QuitPlanResponse response = quitPlanService.createQuitPlanForClient(customer, purchasedPlan, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/coaches")
    public ResponseEntity<List<CoachResponse>> getAllCoaches() {
        return ResponseEntity.ok(coachService.getAllCoaches());
    }


    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/coaches/{id}")
    public ResponseEntity<CoachResponse> getCoachById(@PathVariable Long id) {
        return ResponseEntity.ok(coachService.getCoachById(id));
    }

}

