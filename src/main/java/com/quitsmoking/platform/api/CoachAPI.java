package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.UserAccountResponse;
import com.quitsmoking.platform.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@SecurityRequirement(name = "api")
@RequestMapping("api/coaches")
@Tag(name = "Coach Public API")
public class CoachAPI {
    @Autowired
    private UserService userService;

    @GetMapping
    public java.util.List<UserAccountResponse> getAllCoaches() {
        return userService.getAllCoaches();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountResponse> getCoachById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getCoachById(id));
    }
} 