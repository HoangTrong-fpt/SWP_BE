package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.Gender;
import com.quitsmoking.platform.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AdminUpdateUserRequest {
    private String fullName;
    private Boolean premium;
    private String avatarUrl;
    private Gender gender;
    private Role role;
}
