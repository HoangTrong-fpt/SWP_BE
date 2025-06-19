package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.*;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.enums.Role;
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

@RestController
@CrossOrigin("*")
@RequestMapping("api/user/me")
@SecurityRequirement(name = "api")
@PreAuthorize("isAuthenticated()")
@Tag(name = "User Self Account")

public class UserAPI {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<UserAccountResponse> getMyInfo(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(userService.getSelfInfo(account));
    }

    @PutMapping
    public ResponseEntity<UserAccountResponse> updateMyInfo(@AuthenticationPrincipal Account account,
                                                            @RequestBody @Valid UserUpdateRequest req) {
        return ResponseEntity.ok(userService.updateSelf(account, req));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMyAccount(@AuthenticationPrincipal Account account) {
        if (account.getRole() == Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin không được tự xoá tài khoản");
        }
        return ResponseEntity.ok(userService.deleteSelf(account));
    }

}
