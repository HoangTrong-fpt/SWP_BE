package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.FreeQuitPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FreeQuitPlanRepository extends JpaRepository<FreeQuitPlan, Long> {
    List<FreeQuitPlan> findByAccount(Account account);
    Optional<FreeQuitPlan> findByAccountAndActiveTrue(Account account);
    Optional<FreeQuitPlan> findFirstByAccountOrderByStartDateDesc(Account account);

}