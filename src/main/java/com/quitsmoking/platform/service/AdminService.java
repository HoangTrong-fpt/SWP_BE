package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.AdminAccountResponse;
import com.quitsmoking.platform.dto.AdminCreateUserRequest;
import com.quitsmoking.platform.dto.AdminUpdateUserRequest;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Coach;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.CoachRepository;
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
    private CoachRepository coachRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Account getAccountById(Long id) {
        return authenticationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    public List<AdminAccountResponse> getListUser() {
        return authenticationRepository.findAll().stream()
                .map(account -> {
                    AdminAccountResponse dto = modelMapper.map(account, AdminAccountResponse.class);
                    if (account.getRole() == Role.COACH) {
                        coachRepository.findByAccountUsername(account.getUsername())
                                .ifPresent(coach -> dto.setCoachDescription(coach.getDescription()));
                    }
                    return dto;
                })
                .toList();
    }

    public AdminAccountResponse getUserById(Long id) {
        Account account = authenticationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        AdminAccountResponse dto = modelMapper.map(account, AdminAccountResponse.class);

        if (account.getRole() == Role.COACH) {
            coachRepository.findByAccountUsername(account.getUsername())
                    .ifPresent(coach -> dto.setCoachDescription(coach.getDescription()));
        }

        return dto;
    }



    public AdminAccountResponse createUser(AdminCreateUserRequest req) {
        Account account = authenticationService.registerAdmin(req);

        // Nếu là COACH thì tạo bản ghi bảng Coach
        if (req.getRole() == Role.COACH) {
            Coach coach = new Coach();
            coach.setAccount(account);
            coach.setFullName(account.getFullName());
            coach.setEmail(account.getEmail());
            coach.setPhone(account.getPhoneNumber());
            coach.setAvatarUrl(account.getAvatarUrl());
            coach.setDescription(req.getCoachDescription() != null ? req.getCoachDescription() : "");
            coachRepository.save(coach);
        }

        return modelMapper.map(account, AdminAccountResponse.class);
    }

    public AdminAccountResponse updateUser(Long id, AdminUpdateUserRequest request, Account currentAdmin) {
        if (currentAdmin.getId().equals(id)) {
            throw new ForbiddenException("Bạn không thể cập nhật tài khoản của chính mình tại đây.");
        }

        Account user = authenticationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new ForbiddenException("Không được chỉnh sửa thông tin của admin khác.");
        }

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());

        if (request.getRole() != null && request.getRole() != user.getRole()) {
            if (request.getRole() == Role.ADMIN) {
                throw new ForbiddenException("Không thể cấp quyền ADMIN cho người dùng khác.");
            }
            user.setRole(request.getRole());
        }

        Account updated = authenticationRepository.save(user);

        // Nếu là COACH thì cập nhật thông tin coach profile
        if (updated.getRole() == Role.COACH) {
            coachRepository.findByAccountUsername(updated.getUsername()).ifPresent(coach -> {
                coach.setFullName(updated.getFullName());
                coach.setEmail(updated.getEmail());
                coach.setPhone(updated.getPhoneNumber());
                coach.setAvatarUrl(updated.getAvatarUrl());
                coachRepository.save(coach);
            });
        }

        return modelMapper.map(updated, AdminAccountResponse.class);
    }

    public String deleteUser(Long id, Account currentAdmin) {
        if (currentAdmin.getId().equals(id)) {
            throw new ForbiddenException("Admin không được tự xoá chính mình");
        }

        Account user = authenticationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        user.setActive(false);
        authenticationRepository.save(user);

        // Nếu là COACH thì xóa coach profile
        if (user.getRole() == Role.COACH) {
            coachRepository.findByAccountUsername(user.getUsername())
                    .ifPresent(coachRepository::delete);
        }

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
