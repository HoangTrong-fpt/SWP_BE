package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.Gender;
import com.quitsmoking.platform.enums.Role;
import lombok.Data;

@Data
public class AdminAccountResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private Gender gender;
    private String avatarUrl;
    private Boolean active;
    private Boolean premium;
}
