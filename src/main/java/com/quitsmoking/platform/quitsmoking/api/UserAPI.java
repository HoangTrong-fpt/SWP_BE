package com.quitsmoking.platform.quitsmoking.api;

import com.quitsmoking.platform.quitsmoking.dto.AccountResponse;
import com.quitsmoking.platform.quitsmoking.dto.AdminCreateUserRequest;
import com.quitsmoking.platform.quitsmoking.dto.UpdateUserRequest;
import com.quitsmoking.platform.quitsmoking.dto.UserProfileUpdateRequest;
import com.quitsmoking.platform.quitsmoking.entity.Account;
import com.quitsmoking.platform.quitsmoking.enums.Role;
import com.quitsmoking.platform.quitsmoking.service.UserService;
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


                                // --- ADMIN CRUD --

    @Operation(summary = "Lấy danh sách tất cả người dùng (ADMIN ONLY)")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity getListUser() {
        List<Account> accounts = userService.getListUser();
        List<AccountResponse> responses = accounts.stream()
                .map(account -> modelMapper.map(account, AccountResponse.class))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "tạo người dùng (ADMIN ONLY)")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid AdminCreateUserRequest req) {
        Account created = userService.createUserByAdmin(req);
        AccountResponse response = modelMapper.map(created, AccountResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "cập nhật người dùng (ADMIN ONLY)")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserByAdmin(@PathVariable Long id, @RequestBody UpdateUserRequest req) {
        Account updated = userService.updateUser(id, req);
        AccountResponse response = modelMapper.map(updated, AccountResponse.class);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "xóa người dùng (ADMIN ONLY)")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserByAdmin(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }


                                    // --- DEV CRUD --
    @Operation(summary = "Xoá toàn bộ database BE dùng (ADMIN ONLY)")
    @DeleteMapping("/dev/purge-all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> purgeAllAccounts() {
        userService.purgeAllAccounts();
        return ResponseEntity.ok("All accounts deleted and auto-increment reset.");
    }




                                    // --- USER SELF CRUD ---

    @Operation(summary = "Người dùng lấy thông tin của chính mình")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal Account account) {
        AccountResponse response = modelMapper.map(account, AccountResponse.class);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Người dùng cập nhật của chính mình")
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public ResponseEntity<?> updateMyInfo(@AuthenticationPrincipal Account account,
                                          @RequestBody UserProfileUpdateRequest req) {
        account.setFullName(req.getFullName());
        account.setPhoneNumber(req.getPhoneNumber());
        account.setAvatarUrl(req.getAvatarUrl());
        account.setGender(req.getGender());
        userService.save(account);

        AccountResponse response = modelMapper.map(account, AccountResponse.class);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Người dùng xóa tk của chính mình")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUserAccount(@AuthenticationPrincipal Account account) {
        if (account.getRole() == Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin không được tự xoá tài khoản");
        }
        userService.deleteUser(account.getId());
        return ResponseEntity.ok("Account deleted successfully");
    }








}
