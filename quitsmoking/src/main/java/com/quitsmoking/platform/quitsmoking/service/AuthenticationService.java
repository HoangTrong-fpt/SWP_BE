package com.quitsmoking.platform.quitsmoking.service;

import com.quitsmoking.platform.quitsmoking.dto.AccountResponse;
import com.quitsmoking.platform.quitsmoking.dto.LoginRequest;
import com.quitsmoking.platform.quitsmoking.dto.RegisterRequest;
import com.quitsmoking.platform.quitsmoking.entity.Account;
import com.quitsmoking.platform.quitsmoking.enums.Role;
import com.quitsmoking.platform.quitsmoking.exception.exceptions.AuthenticationException;
import com.quitsmoking.platform.quitsmoking.repository.AuthenticationRepository;
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

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private TokenService tokenService;

    public Account register(RegisterRequest registerRequest){
        Account account = new Account();
        account.setEmail(registerRequest.getEmail());
        account.setFullName(registerRequest.getFullName());
        account.setUsername(registerRequest.getUsername());
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setRole(Role.CUSTOMER);
      try {
          account = authenticationRepository.save(account);
      }catch (DataIntegrityViolationException e){
          if(e.getMessage().contains("account.UKq0uja26qgu1atulenwup9rxyr")){
              throw new DataIntegrityViolationException("Email already exists");
          }else{
              throw new DataIntegrityViolationException("Username already exists");
          }

      }
        return account;
    }

    public AccountResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            ));
        } catch (Exception e) {
            System.out.println("Thong tin ko chinh xac");

            throw new AuthenticationException("Invalid email or password");

        }
        Account account = authenticationRepository.findAccountByUsername(loginRequest.getUsername());
        AccountResponse accountResponse = modelMapper.map(account, AccountResponse.class);
        String token = tokenService.generateToken(account);
        accountResponse.setToken(token);
        return accountResponse ;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return authenticationRepository.findAccountByUsername(username) ;
    }
}
