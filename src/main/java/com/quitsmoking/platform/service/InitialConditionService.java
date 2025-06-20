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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service

public class InitialConditionService {
    @Autowired
    private InitialConditionRepository initialConditionRepository;

    @Autowired
    private AuthenticationRepository accountRepository;

    @Autowired
    private QuitPlanRepository quitPlanRepository;

    public void saveInitialCondition(String email, InitialConditionRequest request) {
        Account account = getAccountByEmail(email);

        if (initialConditionRepository.findByAccountAndIsActiveTrue(account).isPresent()) {
            throw new IllegalStateException("Initial condition already exists");
        }

        InitialCondition ic = new InitialCondition();
        ic.setAccount(account);
        ic.setCigarettesPerDay(request.getCigarettesPerDay());
        ic.setFirstSmokeTime(request.getFirstSmokeTime());
        ic.setQuitReason(request.getQuitReason());
        ic.setIntentionSince(request.getIntentionSince());
        ic.setReadinessScale(request.getReadinessScale());
        ic.setEmotion(request.getEmotion());
        ic.setAddictionLevel(classifyAddiction(request.getCigarettesPerDay(), request.getReadinessScale()));
        ic.setCreatedAt(LocalDateTime.now());
        ic.setVersion(1);
        ic.setActive(true);
        ic.setType(account.getPremium() ? InitialConditionType.PLAN_BOUND : InitialConditionType.FREE_UPDATE);

        initialConditionRepository.save(ic);
    }

    public InitialCondition getMyInitialCondition(String email) {
        Account account = getAccountByEmail(email);
        return initialConditionRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalStateException("Initial condition not found"));
    }

    public void updateInitialCondition(String email, InitialConditionRequest request) {
        Account account = getAccountByEmail(email);

        boolean isPremium = account.getPremium();

        // Kiểm tra premium đã có quit plan chưa
        if (isPremium && quitPlanExists(account)) {
            throw new IllegalStateException("Bạn đang có kế hoạch active, vui lòng hủy trước khi cập nhật thông tin.");
        }

        // set InitialCondition hiện tại về inactive
        initialConditionRepository.findByAccountAndIsActiveTrue(account)
                .ifPresent(currentActive -> {
                    currentActive.setActive(false);
                    initialConditionRepository.save(currentActive);
                });

        // Tạo mới InitialCondition
        InitialCondition ic = new InitialCondition();
        ic.setAccount(account);
        ic.setCigarettesPerDay(request.getCigarettesPerDay());
        ic.setFirstSmokeTime(request.getFirstSmokeTime());
        ic.setQuitReason(request.getQuitReason());
        ic.setIntentionSince(request.getIntentionSince());
        ic.setReadinessScale(request.getReadinessScale());
        ic.setEmotion(request.getEmotion());
        ic.setAddictionLevel(classifyAddiction(request.getCigarettesPerDay(), request.getReadinessScale()));
        ic.setCreatedAt(LocalDateTime.now());

        int newVersion = initialConditionRepository.findMaxVersionByAccount(account).orElse(0) + 1;
        ic.setVersion(newVersion);
        ic.setActive(true);
        ic.setType(isPremium ? InitialConditionType.PLAN_BOUND : InitialConditionType.FREE_UPDATE);

        initialConditionRepository.save(ic);
    }

    private boolean quitPlanExists(Account account) {
        // logic kiểm tra user đã có kế hoạch active hay chưa
        return quitPlanRepository.existsByAccountAndStatus(account, PlanStatus.ACTIVE);
    }


    private Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private AddictionLevel classifyAddiction(int cigarettesPerDay, int readinessScale) {
        if (cigarettesPerDay <= 5 && readinessScale >= 7) return AddictionLevel.LIGHT;
        if (cigarettesPerDay <= 15) return AddictionLevel.MODERATE;
        return AddictionLevel.SEVERE;
    }


}
