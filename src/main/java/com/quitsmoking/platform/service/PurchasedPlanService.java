package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.PurchasedPlanRequest;
import com.quitsmoking.platform.dto.PurchasedPlanResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Coach;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.PaymentStatus;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
import com.quitsmoking.platform.exception.exceptions.IllegalRequestException;
import com.quitsmoking.platform.exception.exceptions.NotFoundException;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.CoachRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchasedPlanService {
    @Autowired
    private PurchasedPlanRepository purchasedPlanRepository;

    @Autowired
    private AuthenticationRepository accountRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public PurchasedPlanResponse activatePurchasedPlan(Long planId, String username) {
        Account account = getAccountByUsername(username);
        PurchasedPlan plan = purchasedPlanRepository.findByIdAndAccountAndUsedFalse(planId, account)
                .orElseThrow(() -> new IllegalRequestException("Gói không tồn tại hoặc đã sử dụng"));

        plan.setActivationDate(LocalDate.now());
        plan.setPaymentStatus(PaymentStatus.PAID); // Gọi là đã kích hoạt và thanh toán
        PurchasedPlan updatedPlan = purchasedPlanRepository.save(plan);

        PurchasedPlanResponse response = modelMapper.map(updatedPlan, PurchasedPlanResponse.class);
        response.setIsActive(isActive(updatedPlan));
        return response;
    }

    public List<PurchasedPlanResponse> getUserPurchasedPlans(String username) {
        Account account = getAccountByUsername(username);
        return purchasedPlanRepository.findAllByAccount(account)
                .stream()
                .map(plan -> {
                    PurchasedPlanResponse res = modelMapper.map(plan, PurchasedPlanResponse.class);
                    res.setIsActive(isActive(plan));
                    return res;
                })
                .toList();
    }

    public PurchasedPlanResponse buyPlan(String username, PurchasedPlanRequest request) {
        Account account = getAccountByUsername(username);
        PurchasedPlan plan = new PurchasedPlan();
        plan.setAccount(account);
        plan.setTemplateType(request.getTemplateType());
        plan.setUsed(false);
        plan.setPurchasedAt(LocalDateTime.now());
        plan.setActivationDate(null);
        plan.setPaymentStatus(PaymentStatus.PENDING);

//        if(request.getCoachId() != null) {
//            // Lấy coach từ DB, set vào plan (giả sử bạn có CoachRepository)
//            Coach coach = coachRepository.findById(request.getCoachId())
//                    .orElseThrow(() -> new NotFoundException("Coach not found"));
//            plan.setCoach(coach);
//        }

        PurchasedPlan savedPlan = purchasedPlanRepository.save(plan);

        PurchasedPlanResponse res = modelMapper.map(savedPlan, PurchasedPlanResponse.class);
        res.setIsActive(isActive(savedPlan));
        return res;
    }


    private boolean isActive(PurchasedPlan plan) {
        return plan.getActivationDate() != null && !Boolean.TRUE.equals(plan.getUsed());
    }

    private Account getAccountByUsername(String username) {
        return accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User không tồn tại"));
    }
}
