package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.InitialCondition;
import com.quitsmoking.platform.entity.QuitPlan;
import com.quitsmoking.platform.enums.InitialConditionType;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.InitialConditionRepository;
import com.quitsmoking.platform.repository.QuitPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class QuitPlanService {
    @Autowired
    private QuitPlanRepository quitPlanRepository;

    @Autowired
    private InitialConditionRepository initialConditionRepository;

    @Autowired
    private AuthenticationRepository accountRepository;

    public void createQuitPlan(String email, QuitPlanRequest request) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (quitPlanRepository.existsByAccountAndStatus(account, PlanStatus.ACTIVE)) {
            throw new IllegalStateException("Bạn đã có kế hoạch active.");
        }

        InitialCondition ic = initialConditionRepository.findById(request.getInitialConditionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid initial condition"));

        QuitPlan plan = new QuitPlan();
        plan.setAccount(account);
        plan.setInitialCondition(ic);
        plan.setTargetQuitDate(request.getTargetQuitDate());
        plan.setMotivationReason(request.getMotivationReason());
        plan.setMethod(request.getMethod());
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setStartDate(request.getStartDate());
        plan.setGoal(request.getGoal());

        quitPlanRepository.save(plan);

        // cập nhật InitialCondition liên kết plan (premium)
        if (account.getPremium()) {
            ic.setLinkedQuitPlanId(plan.getId());
            ic.setType(InitialConditionType.PLAN_BOUND);
            initialConditionRepository.save(ic);
        }
    }

    public QuitPlanResponse getActiveQuitPlan(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        QuitPlan plan = quitPlanRepository.findByAccountAndStatus(account, PlanStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("No active plan found"));

        return mapToResponse(plan);
    }

    public void cancelQuitPlan(String email, Long planId) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        QuitPlan plan = quitPlanRepository.findByIdAndAccount(planId, account)
                .orElseThrow(() -> new IllegalArgumentException("Invalid quit plan ID"));

        plan.setStatus(PlanStatus.CANCELLED);
        quitPlanRepository.save(plan);

        // xóa liên kết với initial_condition nếu có
        InitialCondition ic = plan.getInitialCondition();
        if (ic != null && account.getPremium()) {
            ic.setLinkedQuitPlanId(null);
            ic.setType(InitialConditionType.FREE_UPDATE);
            initialConditionRepository.save(ic);
        }
    }

    private QuitPlanResponse mapToResponse(QuitPlan plan) {
        QuitPlanResponse res = new QuitPlanResponse();
        res.setId(plan.getId());
        res.setTargetQuitDate(plan.getTargetQuitDate());
        res.setMotivationReason(plan.getMotivationReason());
        res.setMethod(plan.getMethod());
        res.setStatus(plan.getStatus());
        res.setStartDate(plan.getStartDate());
        res.setGoal(plan.getGoal());
        return res;
    }
}
