package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserUpdateRequest {
    private String fullName;
    private String avatarUrl;
    private Gender gender;
}
