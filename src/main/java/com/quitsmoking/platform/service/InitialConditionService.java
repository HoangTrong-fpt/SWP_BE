package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.InitialConditionRequest;
import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.entity.QuitPlan;
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

    public void saveInitialCondition(String username, InitialConditionRequest request) {
        Account account = getAccountByUsername(username);

        if (initialConditionRepository.findByAccountAndIsActiveTrue(account).isPresent()) {
            throw new IllegalStateException("Initial condition already exists");
        }

        InitialCondition ic = buildInitialCondition(account, request);
        ic.setVersion(1);
        ic.setActive(true);
        ic.setType(account.getPremium() ? InitialConditionType.PLAN_BOUND : InitialConditionType.FREE_UPDATE);

        initialConditionRepository.save(ic);
    }

    public InitialCondition getMyInitialCondition(String username) {
        Account account = getAccountByUsername(username);
        return initialConditionRepository.findByAccountAndIsActiveTrue(account)
                .orElseThrow(() -> new IllegalStateException("Initial condition not found"));
    }

    public void updateInitialCondition(String username, InitialConditionRequest request) {
        Account account = getAccountByUsername(username);

        if (account.getPremium() && quitPlanRepository.existsByAccountAndStatus(account, PlanStatus.ACTIVE)) {
            throw new IllegalStateException("Bạn đang có kế hoạch active, vui lòng hủy trước khi cập nhật thông tin.");
        }

        initialConditionRepository.findByAccountAndIsActiveTrue(account)
                .ifPresent(current -> {
                    current.setActive(false);
                    initialConditionRepository.save(current);
                });

        InitialCondition ic = buildInitialCondition(account, request);
        int newVersion = initialConditionRepository.findMaxVersionByAccount(account).orElse(0) + 1;
        ic.setVersion(newVersion);
        ic.setActive(true);
        ic.setType(account.getPremium() ? InitialConditionType.PLAN_BOUND : InitialConditionType.FREE_UPDATE);

        initialConditionRepository.save(ic);
    }

    private InitialCondition buildInitialCondition(Account account, InitialConditionRequest request) {
        InitialCondition ic = new InitialCondition();
        ic.setAccount(account);
        ic.setCigarettesPerDay(request.getCigarettesPerDay());
        ic.setFirstSmokeTime(request.getFirstSmokeTime());
        ic.setQuitReason(request.getQuitReason());
        ic.setIntentionSince(request.getIntentionSince());
        ic.setReadinessScale(request.getReadinessScale());
        ic.setEmotion(request.getEmotion());
        ic.setCreatedAt(LocalDateTime.now());
        ic.setAddictionLevel(classifyAddiction(request.getCigarettesPerDay(), request.getReadinessScale()));
        return ic;
    }

    private AddictionLevel classifyAddiction(int cigarettesPerDay, int readinessScale) {
        if (cigarettesPerDay <= 5 && readinessScale >= 7) return AddictionLevel.LIGHT;
        if (cigarettesPerDay <= 15) return AddictionLevel.MODERATE;
        return AddictionLevel.SEVERE;
    }

    private Account getAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username));
    }
}
