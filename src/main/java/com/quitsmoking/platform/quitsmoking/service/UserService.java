package com.quitsmoking.platform.quitsmoking.service;

import com.quitsmoking.platform.quitsmoking.dto.AdminCreateUserRequest;
import com.quitsmoking.platform.quitsmoking.dto.UpdateUserRequest;
import com.quitsmoking.platform.quitsmoking.entity.Account;
import com.quitsmoking.platform.quitsmoking.repository.AuthenticationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthenticationService authenticationService;

    public List<Account> getListUser() {
        return authenticationRepository.findByActiveTrue();
    }

    public Account getUserById(Long id) {
        return authenticationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public Account updateUser(Long id, UpdateUserRequest request) {
        Account user = getUserById(id);
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getPremium() != null) {
            user.setPremium(request.getPremium());
        }
        // không cho update username, email, role từ đây
        return authenticationRepository.save(user);
    }

    public void save(Account account) {
        authenticationRepository.save(account);
    }


    public void deleteUser(Long id) {
        Account user = getUserById(id);
        user.setActive(false); // Soft delete
        authenticationRepository.save(user);
    }

    public Account createUserByAdmin(AdminCreateUserRequest req) {
        // tái sử dụng registerCore từ AuthenticationService
        return authenticationService.registerCore(
                req.getEmail(),
                req.getUsername(),
                req.getPassword(),
                req.getFullName(),
                req.getRole()
        );
    }


//=================================dev code========================================
    @Transactional
    public void purgeAllAccounts() {
        authenticationRepository.deleteAll();
        authenticationRepository.resetAutoIncrement();
    }
//===================================================================================



}
