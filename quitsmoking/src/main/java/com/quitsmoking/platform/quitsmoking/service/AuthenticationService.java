package com.quitsmoking.platform.quitsmoking.service;

import com.quitsmoking.platform.quitsmoking.entity.response.AuthenticationResponse;
import com.quitsmoking.platform.quitsmoking.entity.request.AuthenticationRequest;
import com.quitsmoking.platform.quitsmoking.entity.request.AccountRequest;
import com.quitsmoking.platform.quitsmoking.entity.Account;
import com.quitsmoking.platform.quitsmoking.enums.RoleEnum;
import com.quitsmoking.platform.quitsmoking.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    public Account register(AccountRequest accountRequest){
        // xử lý logic

        // lưu xuống database

        Account account = new Account();

        account.setUsername(accountRequest.getUsername());
        account.setRoleEnum(RoleEnum.CUSTOMER);
        account.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
        account.setFullName(accountRequest.getFullName());
        account.setEmail(accountRequest.getEmail());
        account.setAddress(accountRequest.getAddress());
        account.setPhone(accountRequest.getPhone());

        Account newAccount = authenticationRepository.save(account);
        return newAccount;
    }

    public Account updateProfile(AccountRequest accountRequest) {
        // Tìm account theo ID
        Account account = getCurrentAccount();

        // Cập nhật thông tin (trừ username và role không được thay đổi)
        if (accountRequest.getFullName() != null) {
            account.setFullName(accountRequest.getFullName());
        }

        if (accountRequest.getEmail() != null) {
            account.setEmail(accountRequest.getEmail());
        }

        if (accountRequest.getPhone() != null) {
            account.setPhone(accountRequest.getPhone());
        }

        if (accountRequest.getAddress() != null) {
            account.setPhone(accountRequest.getPhone());
        }

        if (accountRequest.getPassword() != null && !accountRequest.getPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
        }

        return authenticationRepository.save(account);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authenticationRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }


    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()
                    )
            );
        }catch (Exception e){
            throw new NullPointerException("Wrong uername or password");
        }
        Account account = authenticationRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow();
        String token = tokenService.generateToken(account);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setEmail(account.getEmail());
        authenticationResponse.setId(account.getId());
        authenticationResponse.setFullName(account.getFullName());
        authenticationResponse.setUsername(account.getUsername());
        authenticationResponse.setRoleEnum(account.getRoleEnum());
        authenticationResponse.setAddress(account.getAddress());
        authenticationResponse.setPhone(account.getPhone());
        authenticationResponse.setToken(token);

        return authenticationResponse;
    }

    public Account getCurrentAccount() {
        return (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Account block(long userId, boolean isBlocked){
        Account account = authenticationRepository.findById(userId);
        account.setBlocked(isBlocked);
        return authenticationRepository.save(account);
    }

    public List<Account> getAll(){
        return authenticationRepository.findAll();
    }
}