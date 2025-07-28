package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.DailyTaskResponse;
import com.quitsmoking.platform.service.QuitPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/daily-task")
@SecurityRequirement(name = "api")
@Tag(name = "DailyTask")
public class DailyTaskAPI {

    @Autowired
    private QuitPlanService quitPlanService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/today")
    public ResponseEntity<DailyTaskResponse> getTodayTask(Authentication auth) {
        DailyTaskResponse response = quitPlanService.getTodayTask(auth);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/history")
    public ResponseEntity<List<DailyTaskResponse>> getUserDailyTaskHistory(Authentication auth) {
        List<DailyTaskResponse> response = quitPlanService.getUserDailyTaskHistory(auth);
        return ResponseEntity.ok(response);
    }
}

