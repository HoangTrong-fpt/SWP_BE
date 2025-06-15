package com.quitsmoking.platform.quitsmoking.dto;

import com.quitsmoking.platform.quitsmoking.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminCreateUserRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String fullName;

    @Email
    private String email;

    @NotBlank
    private Role role;

    @NotBlank
    private boolean premium = false;
}
