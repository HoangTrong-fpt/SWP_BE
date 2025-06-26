package com.quitsmoking.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quitsmoking.platform.enums.Gender;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.entity.ForgotPassword;
import jakarta.persistence.*;
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
     long id;

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

    @Column(nullable = false)
    private Boolean premium = false;

    @Column(nullable = false)
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    Role role;

    @OneToMany(mappedBy = "account")
    @JsonIgnore
    private List<ForgotPassword> forgotPasswords;

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
    private List<InitialCondition> initialConditions;


}
