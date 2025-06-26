package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordVerifyRequest {
    @Email
    @NotBlank
    private String email;

    @NotNull
    private Integer otp;

    @NotBlank
    private String newPassword;
}
