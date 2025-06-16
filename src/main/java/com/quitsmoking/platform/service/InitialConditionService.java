package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.InitialConditionRequest;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.enums.AddictionLevel;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.InitialConditionRepository;
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

    public void saveInitialCondition(String email, InitialConditionRequest request) {
        Account account = getAccountByEmail(email);

        if (initialConditionRepository.findByAccount(account).isPresent()) {
            throw new IllegalStateException("Initial condition already submitted.");
        }

        InitialCondition ic = new InitialCondition();
        ic.setAccount(account);
        ic.setCigarettesPerDay(request.getCigarettesPerDay());
        ic.setFirstSmokeTime(request.getFirstSmokeTime());
        ic.setAddictionLevel(classifyAddiction(request.getCigarettesPerDay(), request.getReadinessScale()));
        ic.setQuitReason(request.getQuitReason());
        ic.setIntentionSince(request.getIntentionSince());
        ic.setReadinessScale(request.getReadinessScale());
        ic.setEmotion(request.getEmotion());
        ic.setCreatedAt(LocalDateTime.now());

        initialConditionRepository.save(ic);
    }

    public InitialCondition getMyInitialCondition(String email) {
        Account account = getAccountByEmail(email);
        return initialConditionRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalStateException("Initial condition not found"));
    }

    public void updateInitialCondition(String email, InitialConditionRequest request) {
        Account account = getAccountByEmail(email);
        InitialCondition ic = initialConditionRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalStateException("Initial condition not found"));

        ic.setCigarettesPerDay(request.getCigarettesPerDay());
        ic.setFirstSmokeTime(request.getFirstSmokeTime());
        ic.setAddictionLevel(classifyAddiction(request.getCigarettesPerDay(), request.getReadinessScale()));
        ic.setQuitReason(request.getQuitReason());
        ic.setIntentionSince(request.getIntentionSince());
        ic.setReadinessScale(request.getReadinessScale());
        ic.setEmotion(request.getEmotion());

        initialConditionRepository.save(ic);
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
