package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.InitialConditionRequest;
import com.quitsmoking.platform.dto.InitialConditionResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.service.InitialConditionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/initial-condition")
@SecurityRequirement(name = "api")
@Tag(name = "InitialCondition")
public class InitialConditionAPI {
    @Autowired
    private InitialConditionService initialConditionService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<InitialConditionResponse> createInitialCondition(
            @RequestBody @Valid InitialConditionRequest request,
            @AuthenticationPrincipal Account account
    ) {
        InitialCondition ic = initialConditionService.createInitialCondition(account.getUsername(), request);
        InitialConditionResponse response = initialConditionService.toDto(ic); // SỬ DỤNG toDto!
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<InitialConditionResponse> getActiveInitialCondition(
            @AuthenticationPrincipal Account account
    ) {
        InitialCondition ic = initialConditionService.getActiveInitialCondition(account.getUsername());
        InitialConditionResponse response = initialConditionService.toDto(ic); // SỬ DỤNG toDto!
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<InitialConditionResponse> updateInitialCondition(
            @RequestBody @Valid InitialConditionRequest request,
            @AuthenticationPrincipal Account account
    ) {
        InitialCondition ic = initialConditionService.updateInitialCondition(account.getUsername(), request);
        InitialConditionResponse response = initialConditionService.toDto(ic); // SỬ DỤNG toDto!
        return ResponseEntity.ok(response);
    }
}