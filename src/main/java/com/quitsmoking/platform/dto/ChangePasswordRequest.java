package com.quitsmoking.platform.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank
    @Schema(example = "newStrongPass1")
    private String password;

    @NotBlank
    @Schema(example = "newStrongPass1")
    private String repeatPassword;
}
