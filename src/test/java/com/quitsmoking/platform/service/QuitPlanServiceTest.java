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
    void createQuitPlanIgnoresTemplateTypeInRequest() {
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
        req.setPurchasedPlanId(1L);

        when(quitPlanRepo.save(any())).thenAnswer(inv -> {
            QuitPlan q = inv.getArgument(0);
            q.setId(1L);
            return q;
        });

        QuitPlanResponse res = service.createQuitPlan("user", req);
        assertNotNull(res);
    }

    @Test
    void lightTemplateDurationIsCorrected() throws Exception {
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
        LocalDate start = LocalDate.now();
        req.setStartDate(start);
        req.setTargetQuitDate(start.plusDays(50));
        req.setGoal("goal");
        req.setMotivationReason("why");
        req.setMethod(MethodType.TEMPLATE);
        req.setPurchasedPlanId(1L);

        when(quitPlanRepo.save(any())).thenAnswer(inv -> {
            QuitPlan q = inv.getArgument(0);
            q.setId(2L);
            return q;
        });

        QuitPlanResponse res = service.createQuitPlan("user", req);

        assertEquals(start.plusDays(29), res.getTargetQuitDate());
        ObjectMapper om = new ObjectMapper();
        assertEquals(30, om.readValue(res.getPlanDetail(), java.util.List.class).size());
    }

    @Test
    void mediumTemplateDurationIsCorrected() throws Exception {
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
        purchased.setTemplateType(PurchasedTemplateType.MEDIUM);
        purchased.setUsed(false);
        when(purchasedPlanRepo.findByIdAndAccountAndUsedFalse(1L, account))
                .thenReturn(Optional.of(purchased));

        QuitPlanRequest req = new QuitPlanRequest();
        LocalDate start = LocalDate.now();
        req.setStartDate(start);
        req.setTargetQuitDate(start.plusDays(10));
        req.setGoal("goal");
        req.setMotivationReason("why");
        req.setMethod(MethodType.TEMPLATE);
        req.setPurchasedPlanId(1L);

        when(quitPlanRepo.save(any())).thenAnswer(inv -> {
            QuitPlan q = inv.getArgument(0);
            q.setId(3L);
            return q;
        });

        QuitPlanResponse res = service.createQuitPlan("user", req);

        assertEquals(start.plusDays(59), res.getTargetQuitDate());
        ObjectMapper om = new ObjectMapper();
        assertEquals(60, om.readValue(res.getPlanDetail(), java.util.List.class).size());
    }

    @Test
    void heavyTemplateDurationIsCorrected() throws Exception {
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
        purchased.setTemplateType(PurchasedTemplateType.HEAVY);
        purchased.setUsed(false);
        when(purchasedPlanRepo.findByIdAndAccountAndUsedFalse(1L, account))
                .thenReturn(Optional.of(purchased));

        QuitPlanRequest req = new QuitPlanRequest();
        LocalDate start = LocalDate.now();
        req.setStartDate(start);
        req.setTargetQuitDate(start.plusDays(10));
        req.setGoal("goal");
        req.setMotivationReason("why");
        req.setMethod(MethodType.TEMPLATE);
        req.setPurchasedPlanId(1L);

        when(quitPlanRepo.save(any())).thenAnswer(inv -> {
            QuitPlan q = inv.getArgument(0);
            q.setId(4L);
            return q;
        });

        QuitPlanResponse res = service.createQuitPlan("user", req);

        assertEquals(start.plusDays(89), res.getTargetQuitDate());
        ObjectMapper om = new ObjectMapper();
        assertEquals(90, om.readValue(res.getPlanDetail(), java.util.List.class).size());
    }

    @Test
    void userCannotCreatePlanWithAnotherUsersPurchase() {
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

        Account accountA = new Account();
        accountA.setUsername("userA");
        Account accountB = new Account();
        accountB.setUsername("userB");

        when(purchasedPlanService.hasUnusedOrActivePlan("userA")).thenReturn(true);
        when(accountRepo.findAccountByUsername("userA")).thenReturn(Optional.of(accountA));
        when(quitPlanRepo.existsByAccountAndStatus(accountA, PlanStatus.ACTIVE)).thenReturn(false);

        InitialCondition ic = new InitialCondition();
        ic.setId(1L);
        when(icRepo.findByAccount(accountA)).thenReturn(Optional.of(ic));

        when(purchasedPlanRepo.findByIdAndAccountAndUsedFalse(1L, accountA))
                .thenReturn(Optional.empty());

        PurchasedPlan otherUsersPlan = new PurchasedPlan();
        otherUsersPlan.setId(1L);
        otherUsersPlan.setAccount(accountB);
        when(purchasedPlanRepo.findByIdAndAccountAndUsedFalse(1L, accountB))
                .thenReturn(Optional.of(otherUsersPlan));

        QuitPlanRequest req = new QuitPlanRequest();
        req.setStartDate(LocalDate.now());
        req.setTargetQuitDate(LocalDate.now().plusDays(5));
        req.setGoal("goal");
        req.setMotivationReason("why");
        req.setMethod(MethodType.TEMPLATE);
        req.setPurchasedPlanId(1L);

        assertThrows(IllegalStateException.class, () -> service.createQuitPlan("userA", req));
    }
}
