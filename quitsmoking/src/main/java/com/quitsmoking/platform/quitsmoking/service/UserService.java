package com.quitsmoking.platform.quitsmoking.service;


import com.quitsmoking.platform.quitsmoking.entity.Account;
import com.quitsmoking.platform.quitsmoking.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    AuthenticationRepository authenticationRepository;


    public List<Account> getListUser() {
        return authenticationRepository.findAll();
    }
}
