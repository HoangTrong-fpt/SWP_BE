package com.quitsmoking.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quitsmoking.platform.enums.Gender;
import com.quitsmoking.platform.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "account")
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    Long id;

    @Column(unique = true)
    String email;

    @Column(unique = true)
    String username;

    @JsonIgnore
    String password;
    String fullName;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Pattern(regexp = "^(\\+84|0)[1-9][0-9]{8,9}$", message = "Số điện thoại không hợp lệ")
    @Column(length = 20)
    private String phoneNumber;

    @Column(nullable = false)
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" +  this.role.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<ForgotPassword> forgotPasswords;

    @OneToOne(mappedBy = "account")
    @JsonIgnore
    private InitialCondition initialCondition;

    @OneToMany(mappedBy = "recipient")
    @JsonIgnore
    private List<Notification> receivedNotifications;

    @OneToMany(mappedBy = "sender")
    @JsonIgnore
    private List<Notification> sentNotifications;


}