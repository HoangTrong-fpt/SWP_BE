package com.quitsmoking.platform.service;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchasedPlanService {
    @Autowired
    private AuthenticationRepository accountRepository;

    @Autowired
    private PurchasedPlanRepository purchasedPlanRepository;

    public PurchasedPlan createPurchasedPlan(String username, String templateTypeRaw) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PurchasedPlan plan = new PurchasedPlan();
        plan.setAccount(account);
        plan.setUsed(false);
        plan.setPurchasedAt(LocalDateTime.now());

        // Validate & convert string to enum
        PurchasedTemplateType type = PurchasedTemplateType.valueOf(templateTypeRaw.toUpperCase());
        plan.setTemplateType(type);

        return purchasedPlanRepository.save(plan);
    }

    public List<PurchasedPlan> getUnusedPlans(String username) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return purchasedPlanRepository.findAllByAccountAndUsedFalse(account);
    }
}
