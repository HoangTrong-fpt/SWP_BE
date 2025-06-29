package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.PurchasedPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchasedPlanRepository extends JpaRepository<PurchasedPlan, Long> {
    Optional<PurchasedPlan> findByAccountAndUsedFalse(Account account);
    Optional<PurchasedPlan> findByIdAndAccount(Long id, Account account);
}
