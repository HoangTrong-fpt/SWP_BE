package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.PackageResponse;
import com.quitsmoking.platform.dto.PaymentRequest;
import com.quitsmoking.platform.dto.PurchasedPlanRequest;
import com.quitsmoking.platform.dto.PurchasedPlanResponse;
import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.entity.Package;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PaymentStatus;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import com.quitsmoking.platform.exception.exceptions.IllegalRequestException;
import com.quitsmoking.platform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchasedPlanService {

    @Autowired private PurchasedPlanRepository purchasedPlanRepo;
    @Autowired private AccountRepository accountRepo;
    @Autowired private PackageRepository packageRepo;
    @Autowired private CoachRepository coachRepo;
    @Autowired private QuitPlanService quitPlanService;
    @Autowired private InitialConditionRepository initialConditionRepository;
    @Autowired private PaymentService paymentService;

    public PurchasedPlanResponse buyPlan(String username, PurchasedPlanRequest request, String clientIp) {
        Account account = accountRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User not found"));

        Package pack = packageRepo.findByCode(request.getPackageCode())
                .orElseThrow(() -> new IllegalRequestException("Package not found"));


        PurchasedPlan plan = new PurchasedPlan();
        plan.setAccount(account);
        plan.setPlanPackage(pack);
        plan.setPurchasedAt(LocalDateTime.now());
        plan.setPaymentStatus(PaymentStatus.PENDING);
        plan.setStatus(PlanStatus.PENDING);
        plan.setUsed(false);
        purchasedPlanRepo.save(plan);

        // Tạo giao dịch thanh toán VNPay
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(pack.getPrice());
        paymentRequest.setDescription("Plan_" + pack.getCode()); // KHÔNG dấu!
        paymentRequest.setPurchasedPlanId(plan.getId());

        String paymentUrl;
        try {
            paymentUrl = paymentService.createPayment(paymentRequest, clientIp);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo thanh toán", e);
        }

        PurchasedPlanResponse response = toResponse(plan);
        response.setPaymentUrl(paymentUrl);
        return response;
    }

    public PurchasedPlanResponse activatePurchasedPlan(Long planId, String username) {
        Account account = accountRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User not found"));

        PurchasedPlan plan = purchasedPlanRepo.findById(planId)
                .orElseThrow(() -> new IllegalRequestException("PurchasedPlan not found"));

        if (!plan.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException("Bạn không sở hữu gói này");
        }

        if (plan.getStatus() != PlanStatus.PENDING) {
            throw new IllegalRequestException("Gói đã được kích hoạt hoặc không hợp lệ");
        }

        if (plan.getPaymentStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalRequestException("Gói chưa được thanh toán");
        }

        plan.setActivationDate(LocalDate.now());
        plan.setStatus(PlanStatus.ACTIVE);
        purchasedPlanRepo.save(plan);

        Package pack = plan.getPlanPackage();
        if (Boolean.FALSE.equals(pack.getCoachSupport())) {
            InitialCondition initialCondition = initialConditionRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalRequestException("Chưa khai báo điều kiện ban đầu"));

            QuitPlanRequest autoRequest = new QuitPlanRequest();
            autoRequest.setPurchasedPlanId(plan.getId());
            autoRequest.setMethod(MethodType.PLAN_SAMPLE);

            quitPlanService.createQuitPlanFromTemplate(account, plan);
        }

        return toResponse(plan);
    }

    public List<PurchasedPlanResponse> getUserPurchasedPlans(String username) {
        Account account = accountRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User not found"));

        return purchasedPlanRepo.findByAccount(account).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PurchasedPlanResponse getActivePlan(String username) {
        Account account = accountRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User not found"));

        PurchasedPlan plan = purchasedPlanRepo
                .findFirstByAccountAndStatus(account, PlanStatus.ACTIVE)
                .orElseThrow(() -> new IllegalRequestException("No active plan"));

        return toResponse(plan);
    }

    public PurchasedPlanResponse getUserPurchasedPlanById(String username, Long planId) {
        Account account = accountRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalRequestException("User not found"));

        PurchasedPlan plan = purchasedPlanRepo.findById(planId)
                .orElseThrow(() -> new IllegalRequestException("Plan not found"));

        if (!plan.getAccount().getId().equals(account.getId())) {
            throw new ForbiddenException("Bạn không sở hữu gói này");
        }

        return toResponse(plan);
    }

    public PurchasedPlanResponse getActivePlanByAccount(Account account) {
        PurchasedPlan plan = purchasedPlanRepo
                .findFirstByAccountAndStatus(account, PlanStatus.ACTIVE)
                .orElseThrow(() -> new IllegalRequestException("User chưa có plan đang hoạt động"));
        return toResponse(plan);
    }

    public List<PurchasedPlanResponse> getPlansByAccount(Account account) {
        return purchasedPlanRepo.findByAccount(account)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PurchasedPlanResponse toResponse(PurchasedPlan plan) {
        PurchasedPlanResponse res = new PurchasedPlanResponse();
        res.setId(plan.getId());
        res.setAccountId(plan.getAccount().getId());
        res.setCoachId(plan.getCoach() != null ? plan.getCoach().getId() : null);
        res.setPurchasedAt(plan.getPurchasedAt());
        res.setActivationDate(plan.getActivationDate());
        res.setPaymentStatus(plan.getPaymentStatus());
        res.setStatus(plan.getStatus());
        res.setPackageInfo(toPackageResponse(plan.getPlanPackage()));
        // Nếu muốn trả về payment gần nhất
        if (plan.getPayments() != null && !plan.getPayments().isEmpty()) {
            Payment lastPayment = plan.getPayments().get(plan.getPayments().size() - 1);
            res.setPaymentUrl(lastPayment.getPaymentUrl());
        }
        return res;
    }


    private PackageResponse toPackageResponse(Package pack) {
        PackageResponse res = new PackageResponse();
        res.setId(pack.getId());
        res.setCode(pack.getCode());
        res.setName(pack.getName());
        res.setDescription(pack.getDescription());
        res.setPrice(pack.getPrice());
        res.setDuration(pack.getDuration());
        res.setCoachSupport(pack.getCoachSupport());
        return res;
    }
}
