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
import jakarta.transaction.Transactional;
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

    @Transactional
    public void createQuitPlan(String username, QuitPlanRequest request) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại"));

        // Nếu đã có kế hoạch ACTIVE, chặn tạo mới
        if (quitPlanRepository.existsByAccountAndStatus(account, PlanStatus.ACTIVE)) {
            throw new IllegalStateException("Bạn đã có kế hoạch đang hoạt động.");
        }

        // Lấy điều kiện ban đầu hiện tại
        InitialCondition ic = initialConditionRepository.findByAccountAndIsActiveTrue(account)
                .orElseThrow(() -> new IllegalStateException("Bạn chưa tạo điều kiện ban đầu."));

        // Nếu là premium và đã bị ràng buộc, không cho tạo mới
        if (account.getPremium() && ic.getType() == InitialConditionType.PLAN_BOUND) {
            throw new IllegalStateException("Bạn đang bị ràng buộc với kế hoạch hiện tại. Hãy hủy kế hoạch cũ để tạo mới.");
        }

        // Tạo kế hoạch
        QuitPlan plan = new QuitPlan();
        plan.setAccount(account);
        plan.setInitialCondition(ic);
        plan.setTargetQuitDate(request.getTargetQuitDate());
        plan.setMotivationReason(request.getMotivationReason());
        plan.setMethod(request.getMethod());
        plan.setStatus(PlanStatus.ACTIVE);
        plan.setStartDate(request.getStartDate());
        plan.setGoal(request.getGoal());

        QuitPlan savedPlan = quitPlanRepository.saveAndFlush(plan);

        // Nếu premium → ràng buộc InitialCondition
        if (account.getPremium()) {
            ic.setLinkedQuitPlanId(savedPlan.getId());
            ic.setType(InitialConditionType.PLAN_BOUND);
            initialConditionRepository.save(ic);
        }
    }

    public QuitPlanResponse getActiveQuitPlan(String username) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại"));

        QuitPlan plan = quitPlanRepository.findByAccountAndStatus(account, PlanStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy kế hoạch đang hoạt động"));

        return mapToResponse(plan);
    }

    @Transactional
    public void cancelQuitPlan(String username, Long planId) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Người dùng không tồn tại"));

        QuitPlan plan = quitPlanRepository.findByIdAndAccount(planId, account)
                .orElseThrow(() -> new IllegalArgumentException("ID kế hoạch không hợp lệ"));

        plan.setStatus(PlanStatus.CANCELLED);
        quitPlanRepository.saveAndFlush(plan);

        // Gỡ ràng buộc nếu là người dùng premium
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
