package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.AdminAccountResponse;
import com.quitsmoking.platform.dto.AdminCreateUserRequest;
import com.quitsmoking.platform.dto.AdminUpdateUserRequest;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.service.AdminService;
import com.quitsmoking.platform.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("api/user")
@SecurityRequirement(name = "api")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin User Management")
public class AdminUserAPI {
    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<List<AdminAccountResponse>> getListUser() {
        return ResponseEntity.ok(adminService.getListUser());
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

}
