package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.*;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
public class UserAPI {
    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;


    // ---------------- ADMIN CRUD ----------------

    @Operation(summary = "Lấy danh sách tất cả người dùng (ADMIN ONLY)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<AdminAccountResponse>> getListUser() {
        List<Account> accounts = userService.getListUser();
        List<AdminAccountResponse> responses = accounts.stream()
                .map(account -> modelMapper.map(account, AdminAccountResponse.class))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Tạo người dùng (ADMIN ONLY)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<AdminAccountResponse> createUser(@RequestBody @Valid AdminCreateUserRequest req) {
        Account created = userService.createUserByAdmin(req);
        AdminAccountResponse response = modelMapper.map(created, AdminAccountResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Cập nhật người dùng (ADMIN ONLY)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AdminAccountResponse> updateUserByAdmin(@PathVariable Long id,
                                                                  @RequestBody AdminUpdateUserRequest req) {
        Account updated = userService.updateUser(id, req);
        AdminAccountResponse response = modelMapper.map(updated, AdminAccountResponse.class);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xóa người dùng (ADMIN ONLY)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserByAdmin(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }


    // ---------------- DEV TOOL ----------------

    @Operation(summary = "Xoá toàn bộ database BE dùng (ADMIN ONLY)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/dev/purge-all")
    public ResponseEntity<String> purgeAllAccounts() {
        userService.purgeAllAccounts();
        return ResponseEntity.ok("All accounts deleted and auto-increment reset.");
    }


    // ---------------- USER SELF CRUD ----------------

    @Operation(summary = "Người dùng lấy thông tin của chính mình")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserAccountResponse> getMyInfo(@AuthenticationPrincipal Account account) {
        UserAccountResponse response = modelMapper.map(account, UserAccountResponse.class);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Người dùng cập nhật của chính mình")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public ResponseEntity<UserAccountResponse> updateMyInfo(@AuthenticationPrincipal Account account,
                                                            @RequestBody UserProfileUpdateRequest req) {
        account.setFullName(req.getFullName());
        account.setAvatarUrl(req.getAvatarUrl());
        account.setGender(req.getGender());
        userService.save(account);

        UserAccountResponse response = modelMapper.map(account, UserAccountResponse.class);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Người dùng xóa tk của chính mình")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteCurrentUserAccount(@AuthenticationPrincipal Account account) {
        if (account.getRole() == Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin không được tự xoá tài khoản");
        }
        userService.deleteUser(account.getId());
        return ResponseEntity.ok("Account deleted successfully");
    }

}
