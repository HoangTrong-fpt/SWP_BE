package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.InitialConditionRequest;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.enums.AddictionLevel;
import com.quitsmoking.platform.enums.InitialConditionType;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.InitialConditionRepository;
import com.quitsmoking.platform.repository.QuitPlanRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service

public class InitialConditionService {

    @Autowired
    private InitialConditionRepository initialConditionRepository;

    @Autowired
    private AuthenticationRepository accountRepository;

    @Autowired
    private QuitPlanRepository quitPlanRepository;

    @Autowired
    private PurchasedPlanRepository purchasedPlanRepository;

    public InitialCondition createInitialCondition(String username, InitialConditionRequest request) {
        Account account = getAccountByUsername(username);

        if (initialConditionRepository.findByAccount(account).isPresent()) {
            throw new IllegalStateException("Initial condition already exists");
        }

        InitialCondition ic = new InitialCondition();
        ic.setAccount(account);
        buildInitialCondition(ic, request);
        ic.setType(purchasedPlanRepository.findByAccountAndUsedFalse(account).isPresent()
                ? InitialConditionType.PLAN_BOUND
                : InitialConditionType.FREE_UPDATE);

        return initialConditionRepository.save(ic);
    }

    public InitialCondition updateInitialCondition(String username, InitialConditionRequest request) {
        Account account = getAccountByUsername(username);

        // Nếu có gói chưa dùng và đang có kế hoạch active thì không cho cập nhật
        if (purchasedPlanRepository.findByAccountAndUsedFalse(account).isPresent() &&
                quitPlanRepository.existsByAccountAndStatus(account, PlanStatus.ACTIVE)) {
            throw new IllegalStateException("Bạn đang có kế hoạch active, vui lòng huỷ trước khi cập nhật khai báo.");
        }

        InitialCondition ic = initialConditionRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("No initial condition"));
        buildInitialCondition(ic, request);
        ic.setType(purchasedPlanRepository.findByAccountAndUsedFalse(account).isPresent()
                ? InitialConditionType.PLAN_BOUND
                : InitialConditionType.FREE_UPDATE);

        return initialConditionRepository.save(ic);
    }

    public InitialCondition getActiveInitialCondition(String username) {
        Account account = getAccountByUsername(username);
        return initialConditionRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("No initial condition"));
    }

    // --- Tiện ích ---
    private Account getAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private AddictionLevel classifyAddictionLevel(int cigarettesPerDay) {
        if (cigarettesPerDay <= 5) return AddictionLevel.LIGHT;
        if (cigarettesPerDay <= 15) return AddictionLevel.MODERATE;
        return AddictionLevel.SEVERE;
    }

    private void buildInitialCondition(InitialCondition ic, InitialConditionRequest request) {
        ic.setCigarettesPerDay(request.getCigarettesPerDay());
        ic.setFirstSmokeTime(request.getFirstSmokeTime());
        ic.setQuitReason(request.getQuitReason());
        ic.setIntentionSince(request.getIntentionSince());
        ic.setReadinessScale(request.getReadinessScale());
        ic.setEmotion(request.getEmotion());
        ic.setStartSmokingAge(request.getStartSmokingAge());
        ic.setPricePerCigarette(request.getPricePerCigarette());
        ic.setCigarettesPerPack(request.getCigarettesPerPack());
        ic.setHasTriedToQuit(request.isHasTriedToQuit());
        ic.setHasHealthIssues(request.isHasHealthIssues());
        ic.setWeightKg(request.getWeightKg());
        ic.setDesiredQuitDate(LocalDate.parse(request.getDesiredQuitDate()));
        ic.setCreatedAt(LocalDateTime.now());
        ic.setAddictionLevel(classifyAddictionLevel(request.getCigarettesPerDay()));
    }
}
