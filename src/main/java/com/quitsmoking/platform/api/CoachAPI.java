package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.CoachResponse;

import com.quitsmoking.platform.dto.DailyTaskRequest;
import com.quitsmoking.platform.dto.DailyTaskResponse;
import com.quitsmoking.platform.service.CoachService;
import com.quitsmoking.platform.service.TemplatePlanBuilder;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('COACH')")
    @PostMapping("/client/{clientId}/daily-task")
    public ResponseEntity<Void> assignDailyTask(@PathVariable Long clientId, @RequestBody DailyTaskRequest request) {
        coachService.assignDailyTask(clientId, request);
        return ResponseEntity.ok().build();
    }


    @PreAuthorize("hasRole('COACH')")
    @GetMapping("/client/{clientId}/daily-tasks")
    public ResponseEntity<List<DailyTaskResponse>> getAllTasks(@PathVariable Long clientId) {
        List<DailyTaskResponse> response = coachService.getAllDailyTasks(clientId);
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasRole('COACH')")
    @GetMapping("/daily-tips")
    public ResponseEntity<List<String>> getDailyTips() {
        return ResponseEntity.ok(templatePlanBuilder.getDailyTips());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'COACH', 'CUSTOMER')")
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