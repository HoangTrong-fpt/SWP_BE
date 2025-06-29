package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordVerifyRequest {
    @Email
    @NotBlank
    @Schema(example = "john@example.com")
    private String email;

    @NotNull
    @Schema(example = "123456")
    private Integer otp;

    @NotBlank
    @Schema(example = "newPassword123")
    private String newPassword;
}
