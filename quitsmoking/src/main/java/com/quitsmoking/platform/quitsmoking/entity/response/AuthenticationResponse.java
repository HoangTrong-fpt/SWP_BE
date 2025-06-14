package com.quitsmoking.platform.quitsmoking.entity.response;

import lombok.Data;
import com.quitsmoking.platform.quitsmoking.enums.RoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
@Data
public class AuthenticationResponse {
    public long id;
    public String fullName;
    public String username;
    public String email;
    public String phone;
    public String address;
    @Enumerated(value = EnumType.STRING)
    public RoleEnum roleEnum;
    public String token;
}
