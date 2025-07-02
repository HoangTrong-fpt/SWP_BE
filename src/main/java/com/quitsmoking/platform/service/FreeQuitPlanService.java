package com.quitsmoking.platform.service;


import com.quitsmoking.platform.dto.FreeQuitPlanRequest;
import com.quitsmoking.platform.dto.FreeQuitPlanResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.FreeQuitPlan;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.FreeQuitPlanRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class FreeQuitPlanService {
    @Autowired
    private FreeQuitPlanRepository freeQuitPlanRepository;

    @Autowired
    private AuthenticationRepository accountRepository;

    @Autowired
    private ModelMapper modelMapper;

    public FreeQuitPlanResponse createFreePlan(String username, FreeQuitPlanRequest request) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        FreeQuitPlan plan = new FreeQuitPlan();
        plan.setAccount(account);
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setGoal(request.getGoal());
        plan.setMotivationReason(request.getMotivationReason());
        plan.setNote(request.getNote());
        plan.setActive(true);

        FreeQuitPlan savedPlan = freeQuitPlanRepository.save(plan);
        return modelMapper.map(savedPlan, FreeQuitPlanResponse.class);
    }

    public FreeQuitPlanResponse getActiveFreePlan(String username) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        FreeQuitPlan plan = freeQuitPlanRepository.findByAccountAndActiveTrue(account)
                .orElseThrow(() -> new IllegalArgumentException("Không có kế hoạch miễn phí đang hoạt động"));

        return modelMapper.map(plan, FreeQuitPlanResponse.class);
    }

    public void cancelFreePlan(String username) {
        Account account = accountRepository.findAccountByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        FreeQuitPlan plan = freeQuitPlanRepository.findByAccountAndActiveTrue(account)
                .orElseThrow(() -> new IllegalArgumentException("Không có kế hoạch miễn phí đang hoạt động"));

        plan.setActive(false);
        freeQuitPlanRepository.save(plan);
    }
}