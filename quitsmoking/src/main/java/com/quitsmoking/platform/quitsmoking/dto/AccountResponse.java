package com.quitsmoking.platform.quitsmoking.dto;

import com.quitsmoking.platform.quitsmoking.enums.RoleEnum;
import lombok.Data;

@Data
public class AccountResponse {
    public String email;
    public String fullName;
    String username;
    public RoleEnum role;
    public String token;
}
