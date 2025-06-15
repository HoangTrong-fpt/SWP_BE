package com.quitsmoking.platform.quitsmoking.dto;

import com.quitsmoking.platform.quitsmoking.enums.Gender;
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
