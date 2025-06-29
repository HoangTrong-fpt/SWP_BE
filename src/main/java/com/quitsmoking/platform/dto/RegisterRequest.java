package com.quitsmoking.platform.dto;

import com.quitsmoking.platform.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Họ tên không được để trống")
    @Schema(example = "John Doe")
    String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Schema(example = "john@example.com")
    String email;

    @NotNull
    @Schema(example = "MALE")
    private Gender gender;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Schema(example = "johndoe")
    String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải chứa ít nhất 6 ký tự")
    @Schema(example = "password123")
    String password;
}
