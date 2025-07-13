package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.CoachResponse;
import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.service.CoachService;
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
@Tag(name = "CoachAPI")
public class CoachAPI {

    @Autowired
    private CoachService coachService;

    @PreAuthorize("hasRole('COACH')")
    @PostMapping("/client/{clientUsername}/plan")
    public ResponseEntity<QuitPlanResponse> createPlanForClient(Authentication auth,
                                                                @PathVariable String clientUsername,
                                                                @RequestBody QuitPlanRequest request) {
        QuitPlanResponse response = coachService.createPlanForClient(auth.getName(), clientUsername, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/coaches")
    public ResponseEntity<List<CoachResponse>> getAllCoaches() {
        return ResponseEntity.ok(coachService.getAllCoaches());
    }

    // 3. CUSTOMER xem coach theo ID
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/coaches/{id}")
    public ResponseEntity<CoachResponse> getCoachById(@PathVariable Long id) {
        return ResponseEntity.ok(coachService.getCoachById(id));
    }
}