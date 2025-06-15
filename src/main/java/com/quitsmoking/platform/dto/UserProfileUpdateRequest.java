package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserProfileUpdateRequest {
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private Gender gender;
}
