package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.PackageResponse;
import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.dto.QuitPlanResponse;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.entity.Package;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.exception.exceptions.IllegalRequestException;
import com.quitsmoking.platform.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public QuitPlanResponse createQuitPlanForClient(Account account, PurchasedPlan purchasedPlan, QuitPlanRequest request) {
        InitialCondition initialCondition = initialConditionRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalRequestException("Chưa khai báo điều kiện ban đầu"));

        quitPlanValidator.validate(account, purchasedPlan, request, initialCondition, true);
        String snapshot = snapshotter.snapshot(initialCondition);

        QuitPlan plan = customPlanBuilder.build(account, purchasedPlan, request, initialCondition, snapshot);

        purchasedPlan.setUsed(true);
        purchasedPlan.setLinkedQuitPlan(plan);
        purchasedPlanRepository.save(purchasedPlan);
        quitPlanRepository.save(plan);

        return mapToResponse(plan);
    }

    @Transactional
    public QuitPlanResponse createQuitPlanFromTemplate(Account account, PurchasedPlan purchasedPlan) {
        InitialCondition initialCondition = initialConditionRepository.findByAccount(account)
                .orElseThrow(() -> new IllegalRequestException("Chưa khai báo điều kiện ban đầu"));

        quitPlanValidator.validate(account, purchasedPlan, null, initialCondition, false);
        String snapshot = snapshotter.snapshot(initialCondition);

        String goal = "Mục tiêu mặc định";
        QuitPlan plan = templatePlanBuilder.build(account, purchasedPlan, initialCondition, goal);


        purchasedPlan.setUsed(true);
        purchasedPlan.setLinkedQuitPlan(plan);
        purchasedPlanRepository.save(purchasedPlan);
        quitPlanRepository.save(plan);

        return mapToResponse(plan);
    }

    public QuitPlanResponse getActiveQuitPlan(Authentication auth) {
        String username = auth.getName();
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User không tồn tại"));

        QuitPlan plan = quitPlanRepository.findByAccountAndStatus(account, PlanStatus.ACTIVE)
                .orElseThrow(() -> new IllegalRequestException("Không có kế hoạch hoạt động"));

        return mapToResponse(plan);
    }

    public void cancelQuitPlan(Authentication auth, Long planId) {
        String username = auth.getName();
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User không tồn tại"));

        QuitPlan plan = quitPlanRepository.findByIdAndAccount(planId, account)
                .orElseThrow(() -> new IllegalRequestException("Không tìm thấy kế hoạch để hủy"));

        plan.setStatus(PlanStatus.CANCELLED);
        quitPlanRepository.save(plan);
    }

    public List<QuitPlanResponse> getHistoryPlans(Authentication auth) {
        String username = auth.getName();
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User không tồn tại"));

        return quitPlanRepository.findAllByAccountOrderByCreatedAtDesc(account)
                .stream().map(this::mapToResponse).toList();
    }

    public QuitPlanResponse getQuitPlanDetail(Authentication auth, Long planId) {
        String username = auth.getName();
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
        res.setStatus(plan.getStatus());
        res.setCreatedAt(plan.getCreatedAt());

        if (plan.getPurchasedPlan() != null) {
            res.setPurchasedPlanId(plan.getPurchasedPlan().getId());
            Package planPackage = plan.getPurchasedPlan().getPlanPackage();
            if (planPackage != null) {
                PackageResponse pkg = new PackageResponse();
                pkg.setId(planPackage.getId());
                pkg.setCode(planPackage.getCode());
                pkg.setName(planPackage.getName());
                pkg.setPrice(planPackage.getPrice());
                pkg.setDescription(planPackage.getDescription());
                pkg.setDuration(planPackage.getDuration());
                pkg.setCoachSupport(planPackage.getCoachSupport());
                res.setPackageInfo(pkg);
            }
        }
        return res;
    }
}