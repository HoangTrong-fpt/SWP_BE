package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.CoachResponse;
import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Coach;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import com.quitsmoking.platform.exception.exceptions.IllegalRequestException;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.CoachRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoachService {
    @Autowired
    private AuthenticationRepository accountRepository;
    @Autowired
    private CoachRepository coachRepository;
    @Autowired
    private PurchasedPlanRepository purchasedPlanRepository;
    @Autowired
    private QuitPlanService quitPlanService;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public QuitPlanResponse createPlanForClient(String coachUsername, String clientUsername, QuitPlanRequest request) {
        Account coachAccount = accountRepository.findAccountByUsername(coachUsername)
                .orElseThrow(() -> new IllegalRequestException("Coach không tồn tại"));

        if (coachAccount.getRole() != Role.COACH) {
            throw new ForbiddenException("Không phải tài khoản Coach");
        }

        Account clientAccount = accountRepository.findAccountByUsername(clientUsername)
                .orElseThrow(() -> new IllegalRequestException("Client không tồn tại"));

        PurchasedPlan purchasedPlan = purchasedPlanRepository.findByIdAndAccountAndUsedFalse(
                        request.getPurchasedPlanId(), clientAccount)
                .orElseThrow(() -> new IllegalRequestException("Gói không tồn tại hoặc đã sử dụng"));

        Coach coach = coachRepository.findByAccountUsername(coachUsername)
                .orElseThrow(() -> new IllegalRequestException("Thông tin Coach không tồn tại"));

        purchasedPlan.setCoach(coach);
        purchasedPlan.setActivationDate(request.getStartDate());
        purchasedPlanRepository.save(purchasedPlan);

        return quitPlanService.createQuitPlanForClient(clientAccount, purchasedPlan, request);
    }

    // Lấy tất cả coach
    public List<CoachResponse> getAllCoaches() {
        List<Coach> coaches = coachRepository.findAll();
        return coaches.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Lấy coach theo ID
    public CoachResponse getCoachById(Long id) {
        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> new IllegalRequestException("Coach không tồn tại"));
        return toResponse(coach);
    }

    // Mapping entity → DTO
    private CoachResponse toResponse(Coach coach) {
        return modelMapper.map(coach, CoachResponse.class);
    }
}