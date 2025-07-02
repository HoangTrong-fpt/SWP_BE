package com.quitsmoking.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import com.quitsmoking.platform.exception.exceptions.IllegalRequestException;
import com.quitsmoking.platform.exception.exceptions.NotFoundException;
import com.quitsmoking.platform.repository.*;
import com.quitsmoking.platform.service.PurchasedPlanService;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
import com.quitsmoking.platform.exception.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service

public class QuitPlanService {
    @Autowired
    private QuitPlanRepository quitPlanRepository;

    @Autowired
    private InitialConditionRepository initialConditionRepository;

    @Autowired
    private AuthenticationRepository accountRepository;

    @Autowired
    private PurchasedPlanRepository purchasedPlanRepository;

    @Autowired
    private TemplatePlanBuilder templatePlanBuilder;

    @Autowired
    private CustomPlanBuilder customPlanBuilder;

    @Autowired
    private QuitPlanValidator quitPlanValidator;

    @Autowired
    private InitialConditionSnapshotter snapshotter;

    @Transactional
    public QuitPlanResponse createQuitPlan(String username, QuitPlanRequest request, boolean coachFlow) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User không tồn tại"));

        PurchasedPlan purchasedPlan = purchasedPlanRepository
                .findByIdAndAccountAndUsedFalse(request.getPurchasedPlanId(), account)
                .orElseThrow(() -> new IllegalRequestException("Gói không tồn tại hoặc đã sử dụng"));

        InitialCondition initialCondition = initialConditionRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalRequestException("Chưa khai báo điều kiện ban đầu"));

        quitPlanValidator.validate(account, purchasedPlan, request, initialCondition, coachFlow);
        String snapshot = snapshotter.snapshot(initialCondition);

        QuitPlan plan;
        switch (request.getMethod()) {
            case TEMPLATE:
                plan = templatePlanBuilder.build(account, purchasedPlan, initialCondition, request.getGoal());
                break;
            case CUSTOM:
                plan = customPlanBuilder.build(account, purchasedPlan, request, initialCondition, snapshot);
                break;
            default:
                throw new IllegalRequestException("Method không hợp lệ");
        }

        purchasedPlan.setUsed(true);
        purchasedPlan.setLinkedQuitPlan(plan);
        purchasedPlanRepository.save(purchasedPlan);

        quitPlanRepository.save(plan);
        return mapToResponse(plan);
    }

    public QuitPlanResponse getActiveQuitPlan(String username) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User không tồn tại"));

        QuitPlan plan = quitPlanRepository.findByAccountAndStatus(account, PlanStatus.ACTIVE)
                .orElseThrow(() -> new IllegalRequestException("Không có kế hoạch hoạt động"));

        return mapToResponse(plan);
    }

    @Transactional
    public void cancelQuitPlan(String username, Long planId) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User không tồn tại"));

        QuitPlan plan = quitPlanRepository.findByIdAndAccount(planId, account)
                .orElseThrow(() -> new IllegalRequestException("Không tìm thấy kế hoạch để hủy"));

        plan.setStatus(PlanStatus.CANCELLED);
        quitPlanRepository.save(plan);
    }

    public List<QuitPlanResponse> getHistoryPlans(String username) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User không tồn tại"));

        return quitPlanRepository.findAllByAccountOrderByCreatedAtDesc(account)
                .stream().map(this::mapToResponse).toList();
    }

    public QuitPlanResponse getQuitPlanDetail(String username, Long planId) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User không tồn tại"));

        QuitPlan plan = quitPlanRepository.findByIdAndAccount(planId, account)
                .orElseThrow(() -> new IllegalRequestException("Không tìm thấy kế hoạch"));

        return mapToResponse(plan);
    }

    private QuitPlanResponse mapToResponse(QuitPlan plan) {
        QuitPlanResponse res = new QuitPlanResponse();
        res.setId(plan.getId());
        res.setInitialConditionId(plan.getInitialConditionId());
        res.setInitialConditionSnapshot(plan.getInitialConditionSnapshot());
        res.setStartDate(plan.getStartDate());
        res.setTargetQuitDate(plan.getTargetQuitDate());
        res.setGoal(plan.getGoal());
        res.setPlanDetail(plan.getPlanDetail());
        res.setMotivationReason(plan.getMotivationReason());
        res.setMethod(plan.getMethod());
        if (plan.getPurchasedPlan() != null) {
            res.setPurchasedPlanId(plan.getPurchasedPlan().getId());
        }
        res.setTemplateType(plan.getTemplateType());
        res.setStatus(plan.getStatus());
        res.setCreatedAt(plan.getCreatedAt());
        return res;
    }
}
