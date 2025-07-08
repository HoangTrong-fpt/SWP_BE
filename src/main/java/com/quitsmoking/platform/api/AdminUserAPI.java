package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.AdminAccountResponse;
import com.quitsmoking.platform.dto.AdminCreateUserRequest;
import com.quitsmoking.platform.dto.AdminUpdateUserRequest;
import com.quitsmoking.platform.dto.PurchasedPlanResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.service.AdminService;
import com.quitsmoking.platform.service.PurchasedPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/user")
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin User Management")
public class AdminUserAPI {
    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<List<AdminAccountResponse>> getListUser() {
        return ResponseEntity.ok(adminService.getListUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminAccountResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getUserById(id));
    }


    @PostMapping
    public ResponseEntity<AdminAccountResponse> createUser(@RequestBody @Valid AdminCreateUserRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.createUser(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminAccountResponse> updateUser(@PathVariable Long id,
                                                           @RequestBody AdminUpdateUserRequest req,
                                                           @AuthenticationPrincipal Account currentAdmin) {
        return ResponseEntity.ok(adminService.updateUser(id, req, currentAdmin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id,
                                             @AuthenticationPrincipal Account currentAdmin) {
        return ResponseEntity.ok(adminService.deleteUser(id, currentAdmin));
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<String> restoreUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.restoreUser(id));
    }

    @Autowired
    private PurchasedPlanService purchasedPlanService;

    @GetMapping("/{id}/active-plan")
    public ResponseEntity<PurchasedPlanResponse> getUserActivePlan(@PathVariable Long id) {
        Account user = adminService.getAccountById(id);
        return ResponseEntity.ok(purchasedPlanService.getActivePlanByAccount(user));
    }

    @GetMapping("/{id}/plans")
    public ResponseEntity<List<PurchasedPlanResponse>> getUserPlans(@PathVariable Long id) {
        Account user = adminService.getAccountById(id);
        return ResponseEntity.ok(purchasedPlanService.getPlansByAccount(user));
    }



}