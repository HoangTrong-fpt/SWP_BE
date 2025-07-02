package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserUpdateRequest {
    @Schema(example = "John Doe")
    private String fullName;
    @Schema(example = "https://example.com/avatar.png")
    private String avatarUrl;
    @Schema(example = "MALE")
    private Gender gender;
    @Schema(example = "0912345678")
    private String phoneNumber;
}