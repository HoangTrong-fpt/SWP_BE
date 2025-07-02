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
import org.springframework.security.core.Authentication;
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

    @PostMapping("/client/{clientUsername}/plan")
    public ResponseEntity<QuitPlanResponse> createPlanForClient(@PathVariable String clientUsername, @RequestBody QuitPlanRequest request, Authentication auth) {
        return ResponseEntity.ok(coachService.createPlanForClient(auth.getName(), clientUsername, request));
    }
}
