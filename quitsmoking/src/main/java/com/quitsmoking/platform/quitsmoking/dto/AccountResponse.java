package com.quitsmoking.platform.quitsmoking.dto;

import com.quitsmoking.platform.quitsmoking.enums.Gender;
import com.quitsmoking.platform.quitsmoking.enums.Role;
import lombok.Data;

@Data
public class AccountResponse {
    public String email;

    public String fullName;
    String username;
    public Role role;
    public String token;
}
