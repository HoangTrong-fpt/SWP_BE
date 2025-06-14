package com.quitsmoking.platform.quitsmoking.entity.request;

import lombok.Data;

@Data
public class AccountRequest {
    public String fullName;
    public String username;
    public String email;
    public String password;
    public String phone;
    public String address;
}