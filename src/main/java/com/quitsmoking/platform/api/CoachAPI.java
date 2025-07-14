package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.CoachResponse;
import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.service.CoachService;
import com.quitsmoking.platform.service.QuitPlanService;
import com.quitsmoking.platform.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.quitsmoking.platform.entity.Account;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.quitsmoking.platform.exception.exceptions.IllegalRequestException;

@RestController
@RequestMapping("/api/coach")
@SecurityRequirement(name = "api")
@Tag(name = "CoachAPI")
public class CoachAPI {

    @Autowired
    private CoachService coachService;

    @Autowired
    private QuitPlanService quitPlanService;

    @Autowired
    private NotificationService notificationService;


    @PreAuthorize("hasRole('COACH')")
    @PostMapping("/client/{username}/plan")
    public ResponseEntity<QuitPlanResponse> createPlanForClient(Authentication auth,
                                                                @PathVariable String username,
                                                                @RequestBody QuitPlanRequest request) {
        QuitPlanResponse response = coachService.createPlanForClient(auth.getName(), username, request);
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

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelQuitPlan(@AuthenticationPrincipal Account user, @PathVariable Long id) {
        quitPlanService.cancelQuitPlan(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(@AuthenticationPrincipal Account user) {
        int count = notificationService.countUnreadByRecipient(user.getId());
        return ResponseEntity.ok(count);
    }
}