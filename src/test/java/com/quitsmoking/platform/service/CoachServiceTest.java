package com.quitsmoking.platform.service;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Coach;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.CoachRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CoachServiceTest {

    @Test
    void coachCannotClaimPlanOfAnotherUser() {
        CoachService service = new CoachService();
        CoachRepository coachRepo = mock(CoachRepository.class);
        PurchasedPlanRepository planRepo = mock(PurchasedPlanRepository.class);
        AuthenticationRepository accountRepo = mock(AuthenticationRepository.class);

        ReflectionTestUtils.setField(service, "coachRepository", coachRepo);
        ReflectionTestUtils.setField(service, "purchasedPlanRepository", planRepo);
        ReflectionTestUtils.setField(service, "accountRepository", accountRepo);

        Account coachAccount = new Account();
        coachAccount.setUsername("coach");
        Coach coach = new Coach();
        coach.setId(1L);
        coach.setAccount(coachAccount);
        when(coachRepo.findByAccountUsername("coach")).thenReturn(Optional.of(coach));

        Account clientAccount = new Account();
        clientAccount.setUsername("client");
        when(accountRepo.findAccountByUsername("client")).thenReturn(Optional.of(clientAccount));

        when(planRepo.findByIdAndAccountAndUsedFalse(1L, clientAccount)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.assignCoachToPlan("coach", "client", 1L));
    }
}
