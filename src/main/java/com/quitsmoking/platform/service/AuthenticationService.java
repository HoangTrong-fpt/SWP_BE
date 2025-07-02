package com.quitsmoking.platform.service;


import com.quitsmoking.platform.dto.*;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.exception.exceptions.AuthenticationException;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Service
public class AuthenticationService implements UserDetailsService {


    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailService emailService;

    public Account register(RegisterRequest registerRequest) {
        Account account = new Account();
        account.setEmail(registerRequest.getEmail());
        account.setFullName(registerRequest.getFullName());
        account.setUsername(registerRequest.getUsername());
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setRole(Role.CUSTOMER);
        account.setGender(registerRequest.getGender());
        account.setActive(true);
        account.setPhoneNumber(registerRequest.getPhoneNumber());

        // Auto avatarUrl nếu client không truyền
        String avatarUrl = "https://ui-avatars.com/api/?name="
                + URLEncoder.encode(registerRequest.getFullName(), StandardCharsets.UTF_8)
                + "&background=00796B&color=fff&size=128";
        account.setAvatarUrl(avatarUrl);

        try {
            Account savedAccount = authenticationRepository.save(account);

            emailService.sendRegisterMail(
                    savedAccount.getEmail(),
                    savedAccount.getFullName(),
                    "https://quitsmoke.fun/login",
                    "Truy cập ngay"
            );
            return savedAccount;
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage() != null && e.getMessage().contains("account.UKq0uja26qgu1atulenwup9rxyr")) {
                throw new DataIntegrityViolationException("Email already exists");
            } else {
                throw new DataIntegrityViolationException("Username already exists");
            }
        }
    }

    public UserAccountResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new AuthenticationException("Invalid Username or password");
        }

        Account account = authenticationRepository.findAccountByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException("Account not found"));

        if (!account.getActive()) {
            throw new AuthenticationException("Account is deactivated");
        }

        UserAccountResponse userAccountResponse = modelMapper.map(account, UserAccountResponse.class);
        String token = tokenService.generateToken(account);
        userAccountResponse.setToken(token);
        return userAccountResponse;
    }

    public Account registerAdmin(AdminCreateUserRequest req) {
        Account account = new Account();
        account.setEmail(req.getEmail());
        account.setFullName(req.getFullName());
        account.setUsername(req.getUsername());
        account.setPassword(passwordEncoder.encode(req.getPassword()));
        account.setRole(req.getRole());
        account.setGender(req.getGender());
        account.setActive(true);
        account.setPhoneNumber(req.getPhoneNumber());

        // Avatar mặc định (nếu FE không truyền lên)
        String avatarUrl = "https://ui-avatars.com/api/?name="
                + URLEncoder.encode(req.getFullName(), StandardCharsets.UTF_8)
                + "&background=00796B&color=fff&size=128";
        account.setAvatarUrl(avatarUrl);

        try {
            return authenticationRepository.save(account);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage() != null && e.getMessage().contains("account.UKq0uja26qgu1atulenwup9rxyr")) {
                throw new DataIntegrityViolationException("Email already exists");
            } else {
                throw new DataIntegrityViolationException("Username already exists");
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authenticationRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}