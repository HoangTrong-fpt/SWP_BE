package com.quitsmoking.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quitsmoking.platform.dto.QuitPlanRequest;
import com.quitsmoking.platform.entity.*;
import com.quitsmoking.platform.enums.MethodType;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.enums.PurchasedTemplateType;
import com.quitsmoking.platform.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuitPlanServiceTest {

    @Test
    void createQuitPlanThrowsWhenTemplateTypeMismatch() {
        QuitPlanService service = new QuitPlanService();

        AuthenticationRepository accountRepo = mock(AuthenticationRepository.class);
        QuitPlanRepository quitPlanRepo = mock(QuitPlanRepository.class);
        InitialConditionRepository icRepo = mock(InitialConditionRepository.class);
        PurchasedPlanRepository purchasedPlanRepo = mock(PurchasedPlanRepository.class);
        PurchasedPlanService purchasedPlanService = mock(PurchasedPlanService.class);

        ReflectionTestUtils.setField(service, "accountRepository", accountRepo);
        ReflectionTestUtils.setField(service, "quitPlanRepository", quitPlanRepo);
        ReflectionTestUtils.setField(service, "initialConditionRepository", icRepo);
        ReflectionTestUtils.setField(service, "purchasedPlanRepository", purchasedPlanRepo);
        ReflectionTestUtils.setField(service, "purchasedPlanService", purchasedPlanService);
        ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());

        Account account = new Account();
        when(accountRepo.findAccountByUsername("user"))
                .thenReturn(Optional.of(account));
        when(purchasedPlanService.hasUnusedOrActivePlan("user"))
                .thenReturn(true);
        when(quitPlanRepo.existsByAccountAndStatus(account, PlanStatus.ACTIVE))
                .thenReturn(false);

        InitialCondition ic = new InitialCondition();
        ic.setId(1L);
        ic.setCigarettesPerDay(10);
        when(icRepo.findByAccount(account)).thenReturn(Optional.of(ic));

        PurchasedPlan purchased = new PurchasedPlan();
        purchased.setId(1L);
        purchased.setTemplateType(PurchasedTemplateType.LIGHT);
        purchased.setUsed(false);
        when(purchasedPlanRepo.findByIdAndAccountAndUsedFalse(1L, account))
                .thenReturn(Optional.of(purchased));

        QuitPlanRequest req = new QuitPlanRequest();
        req.setStartDate(LocalDate.now());
        req.setTargetQuitDate(LocalDate.now().plusDays(5));
        req.setGoal("goal");
        req.setMotivationReason("why");
        req.setMethod(MethodType.TEMPLATE);
        req.setTemplateType("MEDIUM");
        req.setPurchasedPlanId(1L);

        assertThrows(IllegalStateException.class, () -> service.createQuitPlan("user", req));
    }
}
