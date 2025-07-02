package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.Gender;
import com.quitsmoking.platform.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCreateUserRequest {
    @NotBlank
    @Schema(example = "coach01")
    private String username;

    @NotBlank
    @Schema(example = "StrongPass1")
    private String password;

    @NotBlank
    @Schema(example = "Coach Jane")
    private String fullName;

    @Email
    @NotBlank
    @Schema(example = "coach@example.com")
    private String email;

    @NotNull
    @Schema(example = "COACH")
    private Role role;

    @NotNull
    @Schema(example = "FEMALE")
    private Gender gender;

    @Schema(example = "0912345678")
    private String phoneNumber;
}