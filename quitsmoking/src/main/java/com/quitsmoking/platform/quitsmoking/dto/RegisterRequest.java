package com.quitsmoking.platform.quitsmoking.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    String fullName;
    String email;
    String username;
    String password;
}
