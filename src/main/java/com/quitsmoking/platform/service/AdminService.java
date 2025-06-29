package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.AdminAccountResponse;
import com.quitsmoking.platform.dto.AdminCreateUserRequest;
import com.quitsmoking.platform.dto.AdminUpdateUserRequest;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional

public class AdminService {
    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ModelMapper modelMapper;

    public List<AdminAccountResponse> getListUser() {
        return authenticationRepository.findAll().stream()
                .map(account -> modelMapper.map(account, AdminAccountResponse.class))
                .toList();
    }

    public AdminAccountResponse createUser(AdminCreateUserRequest req) {
        Account account = authenticationService.registerAdmin(req);
        return modelMapper.map(account, AdminAccountResponse.class);
    }

    public AdminAccountResponse updateUser(Long id, AdminUpdateUserRequest request, Account currentAdmin) {
        if (currentAdmin.getId() == id) {
            throw new ForbiddenException("Bạn không thể cập nhật tài khoản của chính mình tại đây.");
        }

        Account user = authenticationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        // Ngăn chỉnh sửa admin khác
        if (user.getRole() == Role.ADMIN) {
            throw new ForbiddenException("Không được chỉnh sửa thông tin của admin khác.");
        }

        // Các trường được phép cập nhật
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getGender() != null) user.setGender(request.getGender());

        // Kiểm soát cập nhật role
        if (request.getRole() != null) {
            if (request.getRole() == Role.ADMIN) {
                throw new ForbiddenException("Không thể cấp quyền ADMIN cho người dùng khác.");
            }
            user.setRole(request.getRole());
        }

        Account updated = authenticationRepository.save(user);
        return modelMapper.map(updated, AdminAccountResponse.class);
    }


    public String deleteUser(Long id, Account currentAdmin) {
        if (currentAdmin.getId() == id) {
            throw new ForbiddenException("Admin không được tự xoá chính mình");
        }

        Account user = authenticationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        user.setActive(false);
        authenticationRepository.save(user);
        return "User deleted successfully";
    }

    public String restoreUser(Long id) {
        Account user = authenticationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        user.setActive(true);
        authenticationRepository.save(user);
        return "User restored successfully";
    }
}
