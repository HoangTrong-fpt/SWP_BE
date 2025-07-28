package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SmokingLogRepository  extends JpaRepository<SmokingLog, Long> {
    Optional<SmokingLog> findByAccountAndQuitPlanAndDate(Account account, QuitPlan plan, LocalDate date);
    Optional<SmokingLog> findByAccountAndFreeQuitPlanAndDate(Account account, FreeQuitPlan plan, LocalDate date);

    List<SmokingLog> findAllByAccountAndQuitPlanOrderByDate(Account account, QuitPlan plan);

    Optional<SmokingLog> findByAccountAndPurchasedPlanAndDate(Account account, PurchasedPlan plan, LocalDate date);

    List<SmokingLog> findAllByAccountAndPurchasedPlanOrderByDate(Account account, PurchasedPlan plan);
}
