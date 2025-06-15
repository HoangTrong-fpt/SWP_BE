package com.quitsmoking.platform.quitsmoking.dto;

import com.quitsmoking.platform.quitsmoking.enums.Gender;
import com.quitsmoking.platform.quitsmoking.enums.Role;
import lombok.Data;

@Data
public class AccountResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private Gender gender;
    private String avatarUrl;
    private String phoneNumber;
    private String token;
}
