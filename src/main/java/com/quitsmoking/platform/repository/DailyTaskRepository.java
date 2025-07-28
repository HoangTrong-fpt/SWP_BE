package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.DailyTask;
import com.quitsmoking.platform.entity.PurchasedPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyTaskRepository extends JpaRepository<DailyTask, Long> {
    List<DailyTask> findAllByPurchasedPlanOrderByDateAsc(PurchasedPlan plan);
    Optional<DailyTask> findByPurchasedPlanAndDate(PurchasedPlan plan, LocalDate date);

}
