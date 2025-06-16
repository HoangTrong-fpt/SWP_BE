package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.InitialConditionRequest;
import com.quitsmoking.platform.dto.InitialConditionResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.service.InitialConditionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/initial-condition")
@SecurityRequirement(name = "api")
@CrossOrigin("*")
public class InitialConditionAPI {
    @Autowired
    private InitialConditionService initialConditionService;

    @Autowired
    private ModelMapper modelMapper;

    @Operation(summary = "Ghi nhận tình trạng ban đầu (mỗi tài khoản chỉ 1 lần)")
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> submitInitialCondition(
            @RequestBody @Valid InitialConditionRequest request,
            @AuthenticationPrincipal Account account
    ) {
        initialConditionService.saveInitialCondition(account.getEmail(), request);
        return ResponseEntity.ok("Initial condition submitted successfully");
    }

    @Operation(summary = "Lấy tình trạng ban đầu của người dùng hiện tại")
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<InitialConditionResponse> getInitialCondition(
            @AuthenticationPrincipal Account account
    ) {
        InitialCondition ic = initialConditionService.getMyInitialCondition(account.getEmail());
        InitialConditionResponse dto = modelMapper.map(ic, InitialConditionResponse.class);
        dto.setAddictionLevel(ic.getAddictionLevel()); // gán thêm thủ công
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Cập nhật tình trạng ban đầu")
    @PutMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> updateInitialCondition(
            @RequestBody @Valid InitialConditionRequest request,
            @AuthenticationPrincipal Account account
    ) {
        initialConditionService.updateInitialCondition(account.getEmail(), request);
        return ResponseEntity.ok("Initial condition updated successfully");
    }

}
