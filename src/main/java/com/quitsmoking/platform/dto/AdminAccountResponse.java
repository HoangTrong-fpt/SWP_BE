package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.Gender;
import com.quitsmoking.platform.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdminAccountResponse {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "admin")
    private String username;
    @Schema(example = "Admin User")
    private String fullName;
    @Schema(example = "admin@example.com")
    private String email;
    @Schema(example = "ADMIN")
    private Role role;
    @Schema(example = "MALE")
    private Gender gender;
    @Schema(example = "https://example.com/avatar.png")
    private String avatarUrl;
    @Schema(example = "0912345678")
    private String phoneNumber;
    @Schema(example = "true")
    private Boolean active;
}