package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.Gender;
import com.quitsmoking.platform.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank
    private String email;

    @NotNull
    private Role role;

    @NotNull
    private Gender gender;

    private boolean premium = false;
}
