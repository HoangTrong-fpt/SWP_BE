package com.quitsmoking.platform.service;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import com.quitsmoking.platform.repository.QuitPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PurchasedPlanService {
    @Autowired
    private AuthenticationRepository accountRepository;

    @Autowired
    private PurchasedPlanRepository purchasedPlanRepository;

    @Autowired
    private QuitPlanRepository quitPlanRepository;

    // Map price (in VND) to template type used for generated plans
    private static final Map<Integer, PurchasedTemplateType> PRICE_TO_TYPE = Map.of(
            100_000, PurchasedTemplateType.LIGHT,
            200_000, PurchasedTemplateType.MEDIUM,
            300_000, PurchasedTemplateType.HEAVY,
            500_000, PurchasedTemplateType.COACH
    );

    public PurchasedPlan createPurchasedPlan(String username, int amount) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PurchasedPlan plan = new PurchasedPlan();
        plan.setAccount(account);
        plan.setUsed(false);
        plan.setPurchasedAt(LocalDateTime.now());

        // Map purchase amount to template type
        PurchasedTemplateType type = PRICE_TO_TYPE.get(amount);
        if (type == null) {
            throw new IllegalArgumentException("Invalid purchase amount");
        }
        plan.setTemplateType(type);

        return purchasedPlanRepository.save(plan);
    }

    public List<PurchasedPlan> getUnusedPlans(String username) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return purchasedPlanRepository.findAllByAccountAndUsedFalse(account);
    }

    /**
     * Check if the user has either an unused purchased plan or a quit plan that
     * is currently active.
     */
    public boolean hasUnusedOrActivePlan(String username) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasUnused = purchasedPlanRepository.findByAccountAndUsedFalse(account).isPresent();
        boolean hasActive = quitPlanRepository.existsByAccountAndStatus(account, PlanStatus.ACTIVE);
        return hasUnused || hasActive;
    }
}
