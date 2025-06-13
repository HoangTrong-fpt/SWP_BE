package com.quitsmoking.platform.quitsmoking.service;

import com.quitsmoking.platform.quitsmoking.entity.Account;
import com.quitsmoking.platform.quitsmoking.repository.AuthenticationRepository;
import com.quitsmoking.platform.quitsmoking.exception.exceptions.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<Account> getListUser() {
        return authenticationRepository.findAll();
    }

    public Account getUserById(Long id) {
        return authenticationRepository.findById(id)
                .orElseThrow(() -> new AuthenticationException("User not found with id: " + id));
    }

    public Account createUser(Account account) {
        // Encode password before saving
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return authenticationRepository.save(account);
    }

    public Account updateUser(Long id, Account updatedAccount) {
        Account existingAccount = getUserById(id);
        
        // Update fields
        existingAccount.setEmail(updatedAccount.getEmail());
        existingAccount.setUsername(updatedAccount.getUsername());
        existingAccount.setFullName(updatedAccount.getFullName());
        
        // Only update password if it's provided
        if (updatedAccount.getPassword() != null && !updatedAccount.getPassword().isEmpty()) {
            existingAccount.setPassword(passwordEncoder.encode(updatedAccount.getPassword()));
        }
        
        // Only update role if it's provided
        if (updatedAccount.getRole() != null) {
            existingAccount.setRole(updatedAccount.getRole());
        }
        
        return authenticationRepository.save(existingAccount);
    }

    public void deleteUser(Long id) {
        if (!authenticationRepository.existsById(id)) {
            throw new AuthenticationException("User not found with id: " + id);
        }
        authenticationRepository.deleteById(id);
    }
}
