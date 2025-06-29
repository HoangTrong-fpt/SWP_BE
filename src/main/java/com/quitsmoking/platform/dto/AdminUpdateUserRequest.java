package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.Gender;
import com.quitsmoking.platform.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AdminUpdateUserRequest {
    @Schema(example = "Updated Name")
    private String fullName;
    @Schema(example = "https://example.com/avatar.png")
    private String avatarUrl;
    @Schema(example = "MALE")
    private Gender gender;
    @Schema(example = "CUSTOMER")
    private Role role;
}
