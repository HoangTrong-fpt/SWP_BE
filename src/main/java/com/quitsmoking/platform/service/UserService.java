package com.quitsmoking.platform.service;


import com.quitsmoking.platform.dto.AdminCreateUserRequest;
import com.quitsmoking.platform.dto.AdminUpdateUserRequest;
import com.quitsmoking.platform.dto.ChangePasswordRequest;
import com.quitsmoking.platform.dto.UserAccountResponse;
import com.quitsmoking.platform.dto.UserUpdateRequest;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {
    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserAccountResponse getSelfInfo(Account account) {
        return modelMapper.map(account, UserAccountResponse.class);
    }

    public UserAccountResponse updateSelf(Account account, UserUpdateRequest req) {
        account.setFullName(req.getFullName());
        account.setAvatarUrl(req.getAvatarUrl());
        account.setGender(req.getGender()); // Gender là Enum
        Account saved = authenticationRepository.save(account);
        return modelMapper.map(saved, UserAccountResponse.class);
    }

    public String deleteSelf(Account account) {
        account.setActive(false);
        authenticationRepository.save(account);
        return "Account deleted successfully";
    }

    public String changeMyPassword(Account account, ChangePasswordRequest req) {
        // Kiểm tra mật khẩu mới trùng khớp
        if (!req.getPassword().equals(req.getRepeatPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Kiểm tra mật khẩu cũ có đúng không
        if (!passwordEncoder.matches(req.getOldPassword(), account.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Encode mật khẩu mới
        String encoded = passwordEncoder.encode(req.getPassword());

        // Cập nhật trong DB
        authenticationRepository.updatePassword(account.getEmail(), encoded);

        return "Password changed successfully";
    }
}
