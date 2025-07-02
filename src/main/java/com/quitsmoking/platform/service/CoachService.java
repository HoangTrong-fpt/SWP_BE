package com.quitsmoking.platform.service;

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

    @Transactional
    public QuitPlanResponse createPlanForClient(String coachUsername, String clientUsername, QuitPlanRequest request) {
        Account coachAccount = accountRepository.findAccountByUsername(coachUsername)
                .orElseThrow(() -> new IllegalRequestException("Coach không tồn tại"));

        if (!coachAccount.getRole().name().equals("COACH")) {
            throw new ForbiddenException("Không phải tài khoản Coach");
        }

        Account clientAccount = accountRepository.findAccountByUsername(clientUsername)
                .orElseThrow(() -> new IllegalRequestException("Client không tồn tại"));

        PurchasedPlan plan = purchasedPlanRepository.findByIdAndAccountAndUsedFalse(request.getPurchasedPlanId(), clientAccount)
                .orElseThrow(() -> new IllegalRequestException("Gói không tồn tại hoặc đã sử dụng"));

        Coach coach = coachRepository.findByAccountUsername(coachUsername)
                .orElseThrow(() -> new IllegalRequestException("Thông tin Coach không tồn tại"));

        plan.setCoach(coach);
        plan.setActivationDate(request.getStartDate());
        purchasedPlanRepository.save(plan);

        return quitPlanService.createQuitPlan(clientUsername, request, true);
    }
}
