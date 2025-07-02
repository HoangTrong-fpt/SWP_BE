package com.quitsmoking.platform.service;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Coach;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.CoachRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoachService {
    @Autowired
    private CoachRepository coachRepository;
    @Autowired
    private PurchasedPlanRepository purchasedPlanRepository;
    @Autowired
    private AuthenticationRepository accountRepository;

    public Coach getCoachByUsername(String username) {
        return coachRepository.findByAccountUsername(username)
                .orElseThrow(() -> new ForbiddenException("Coach not found"));
    }

    public void assignCoachToPlan(String coachUsername, String clientUsername, Long planId) {
        Coach coach = getCoachByUsername(coachUsername);
        Account client = getAccountByUsername(clientUsername);

        PurchasedPlan plan = purchasedPlanRepository
                .findByIdAndAccountAndUsedFalse(planId, client)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found or already used"));

        if (plan.getCoach() != null && !plan.getCoach().getId().equals(coach.getId())) {
            throw new IllegalStateException("Plan already assigned to another coach");
        }

        plan.setCoach(coach);
        purchasedPlanRepository.save(plan);
    }

    public List<Account> getClients(String coachUsername) {
        Coach coach = getCoachByUsername(coachUsername);
        return purchasedPlanRepository.findAllByCoach(coach).stream()
                .map(PurchasedPlan::getAccount)
                .distinct()
                .collect(Collectors.toList());
    }

    public Coach createCoachProfile(Account account) {
        if (account.getRole() != Role.COACH) {
            throw new ForbiddenException("Account is not coach role");
        }
        Coach coach = new Coach();
        coach.setAccount(account);
        return coachRepository.save(coach);
    }

    private Account getAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
