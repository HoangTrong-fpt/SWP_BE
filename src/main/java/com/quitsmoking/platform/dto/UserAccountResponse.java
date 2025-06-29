package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.Gender;
import com.quitsmoking.platform.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserAccountResponse {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "johndoe")
    private String username;
    @Schema(example = "John Doe")
    private String fullName;
    @Schema(example = "john@example.com")
    private String email;
    @Schema(example = "CUSTOMER")
    private Role role;
    @Schema(example = "MALE")
    private Gender gender;
    @Schema(example = "https://example.com/avatar.png")
    private String avatarUrl;
    @Schema(example = "jwt-token")
    private String token;
}
