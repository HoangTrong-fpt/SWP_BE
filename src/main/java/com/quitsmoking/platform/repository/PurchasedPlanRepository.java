package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchasedPlanRepository extends JpaRepository<PurchasedPlan, Long> {
    Optional<PurchasedPlan> findByAccountAndUsedFalse(Account account);
    List<PurchasedPlan> findAllByAccountAndUsedFalse(Account account);
    Optional<PurchasedPlan> findByIdAndAccount(Long id, Account account);
    Optional<PurchasedPlan> findByIdAndAccountAndUsedFalse(Long id, Account account);
    List<PurchasedPlan> findAllByCoach(Coach coach);
}
