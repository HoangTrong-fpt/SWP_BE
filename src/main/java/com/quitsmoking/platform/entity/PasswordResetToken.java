package com.quitsmoking.platform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String token;
    private LocalDateTime expiry;

    public PasswordResetToken(String email, String token, LocalDateTime expiry) {
        this.email = email;
        this.token = token;
        this.expiry = expiry;
    }
}
