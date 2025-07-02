package com.quitsmoking.platform.api;



import com.quitsmoking.platform.dto.ForgotPasswordRequest;
import com.quitsmoking.platform.dto.ForgotPasswordVerifyRequest;
import com.quitsmoking.platform.dto.LoginRequest;
import com.quitsmoking.platform.dto.RegisterRequest;
import com.quitsmoking.platform.dto.UserAccountResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.service.AuthenticationService;
import com.quitsmoking.platform.service.ForgotPasswordService;
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
    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody @Valid RegisterRequest registerRequest){
        Account newAccount = authenticationService.register(registerRequest);
        return ResponseEntity.ok(newAccount);
    }

    @PostMapping("/login")
    public ResponseEntity<UserAccountResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        UserAccountResponse account = authenticationService.login(loginRequest);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        forgotPasswordService.sendOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/forgot-password/verify")
    public ResponseEntity<String> verifyForgotPassword(@RequestBody @Valid ForgotPasswordVerifyRequest request) {
        forgotPasswordService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }



}
