package com.quitsmoking.platform.quitsmoking.api;

import com.quitsmoking.platform.quitsmoking.dto.AccountResponse;
import com.quitsmoking.platform.quitsmoking.dto.LoginRequest;
import com.quitsmoking.platform.quitsmoking.dto.RegisterRequest;
import com.quitsmoking.platform.quitsmoking.entity.Account;
import com.quitsmoking.platform.quitsmoking.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@SecurityRequirement(name ="api")
@CrossOrigin("*")
public class AuthenticationAPI {
    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterRequest registerRequest){
        Account newAccount = authenticationService.register(registerRequest);
        return ResponseEntity.ok(newAccount);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequest loginRequest){
        AccountResponse account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }

}
