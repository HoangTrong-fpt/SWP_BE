package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.FreeQuitPlan;
import com.quitsmoking.platform.entity.QuitPlan;
import com.quitsmoking.platform.entity.SmokingLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SmokingLogRepository  extends JpaRepository<SmokingLog, Long> {
    Optional<SmokingLog> findByAccountAndQuitPlanAndDate(Account account, QuitPlan plan, LocalDate date);
    Optional<SmokingLog> findByAccountAndFreeQuitPlanAndDate(Account account, FreeQuitPlan plan, LocalDate date);

    List<SmokingLog> findAllByAccountAndQuitPlanOrderByDate(Account account, QuitPlan plan);
    List<SmokingLog> findAllByAccountAndFreeQuitPlanOrderByDate(Account account, FreeQuitPlan plan);
}
