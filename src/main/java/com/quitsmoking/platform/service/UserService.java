package com.quitsmoking.platform.service;


import com.quitsmoking.platform.dto.ChangePasswordRequest;
import com.quitsmoking.platform.dto.UserAccountResponse;
import com.quitsmoking.platform.dto.UserUpdateRequest;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
        account.setGender(req.getGender());
        account.setPhoneNumber(req.getPhoneNumber());

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

    public UserAccountResponse uploadAvatar(String username, MultipartFile file) {
        Account account = authenticationRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 1. Lưu file vào server/local/cloud (ví dụ lưu local):
        String uploadDir = "uploads/avatars/"; // tạo folder này trong project
        String fileName = username + "_" + System.currentTimeMillis() + ".jpg";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload folder");
            }
        }
        try (InputStream is = file.getInputStream()) {
            Files.copy(is, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar");
        }

        // 2. Set lại avatarUrl (nếu lưu local thì trả url API, còn nếu dùng Cloud thì trả link cloud)
        String avatarUrl = "/uploads/avatars/" + fileName; // ví dụ, FE gọi /uploads/avatars/... sẽ trả file
        account.setAvatarUrl(avatarUrl);
        authenticationRepository.save(account);

        return modelMapper.map(account, UserAccountResponse.class);
    }
}