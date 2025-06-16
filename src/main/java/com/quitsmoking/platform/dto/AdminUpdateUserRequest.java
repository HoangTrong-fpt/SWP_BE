package com.quitsmoking.platform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AdminUpdateUserRequest {
    private String fullName;
    private Boolean premium;

}
