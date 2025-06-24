package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.QuitPlan;
import com.quitsmoking.platform.enums.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuitPlanRepository extends JpaRepository<QuitPlan, Long> {
    boolean existsByAccountAndStatus(Account account, PlanStatus status);

    Optional<QuitPlan> findByAccountAndStatus(Account account, PlanStatus status);

    Optional<QuitPlan> findByIdAndAccount(Long id, Account account);
}
