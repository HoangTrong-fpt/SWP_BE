package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank
    private String password;

    @NotBlank
    private String repeatPassword;
}
