package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.*;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import com.quitsmoking.platform.exception.exceptions.IllegalRequestException;
import com.quitsmoking.platform.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CoachService {
    @Autowired
    private CoachRepository coachRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private QuitPlanRepository quitPlanRepository;
    @Autowired
    private DailyTaskRepository dailyTaskRepository;
    @Autowired
    private PurchasedPlanRepository purchasedPlanRepository;


    // Lấy tất cả coach
    public List<CoachResponse> getAllCoaches() {
        List<Coach> coaches = coachRepository.findAll();
        return coaches.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Lấy coach theo ID
    public CoachResponse getCoachById(Long id) {
        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> new IllegalRequestException("Coach không tồn tại"));
        return toResponse(coach);
    }

    // CoachService - method giao nhiệm vụ
    @Transactional
    public void assignDailyTask(Long clientId, DailyTaskRequest request) {
        Account client = accountRepository.findById(clientId)
                .orElseThrow(() -> new IllegalRequestException("Client not found"));

        // Tìm purchased plan của client
        List<PurchasedPlan> plans = purchasedPlanRepository.findByAccount(client);
        if (plans.isEmpty()) {
            throw new IllegalRequestException("Client không có purchased plan nào");
        }

        // Lấy plan đầu tiên (có thể cần logic phức tạp hơn tùy theo yêu cầu)
        PurchasedPlan plan = plans.get(0);

        // Kiểm tra quyền sở hữu và ACTIVE
        if (!plan.getAccount().getId().equals(client.getId())) {
            throw new ForbiddenException("Gói không thuộc về client này");
        }
        if (Boolean.FALSE.equals(plan.getPlanPackage().getCoachSupport())) {
            throw new IllegalRequestException("Gói này không phải loại có huấn luyện viên");
        }
        if (plan.getStatus() != PlanStatus.ACTIVE) {
            throw new IllegalRequestException("Gói chưa được kích hoạt");
        }

        Optional<DailyTask> existing = dailyTaskRepository.findByPurchasedPlanAndDate(plan, request.getDate());
        DailyTask task = existing.orElseGet(DailyTask::new);

        task.setPurchasedPlan(plan);
        task.setDate(request.getDate());
        task.setTargetSmokePerDay(request.getTargetSmokePerDay());
        task.setNote(request.getNote());

        dailyTaskRepository.save(task);
    }


    public List<DailyTaskResponse> getAllDailyTasks(Long clientId) {
        Account client = accountRepository.findById(clientId)
                .orElseThrow(() -> new IllegalRequestException("Client not found"));

        List<PurchasedPlan> plans = purchasedPlanRepository.findByAccount(client);
        if (plans.isEmpty()) {
            throw new IllegalRequestException("Client không có purchased plan nào");
        }

        // Lấy plan đầu tiên (có thể cần logic phức tạp hơn tùy theo yêu cầu)
        PurchasedPlan plan = plans.get(0);

        return dailyTaskRepository.findAllByPurchasedPlanOrderByDateAsc(plan)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    private DailyTaskResponse mapToResponse(DailyTask task) {
        DailyTaskResponse res = new DailyTaskResponse();
        res.setDate(task.getDate());
        res.setTargetSmokePerDay(task.getTargetSmokePerDay());
        res.setNote(task.getNote());
        res.setCompleted(task.getCompleted());
        res.setUserNote(task.getUserNote());
        return res;
    }

    // Mapping entity → DTO
    private CoachResponse toResponse(Coach coach) {
        return modelMapper.map(coach, CoachResponse.class);
    }
}

