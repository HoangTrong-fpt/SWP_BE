package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.PaymentStatus;
import com.quitsmoking.platform.enums.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.quitsmoking.platform.entity.Package;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PurchasedPlanRepository extends JpaRepository<PurchasedPlan, Long> {
    List<PurchasedPlan> findByAccount(Account account);
    Optional<PurchasedPlan> findFirstByAccountAndStatus(Account account, PlanStatus status);
    Optional<PurchasedPlan> findByAccountAndUsedFalse(Account account);
    Optional<PurchasedPlan> findFirstByAccount_IdAndStatus(Long accountId, PlanStatus status);
    Optional<PurchasedPlan> findFirstByAccountAndPlanPackageAndStatusInAndPaymentStatusIn(
            Account account,
            Package planPackage,
            List<PlanStatus> statusList,
            List<PaymentStatus> paymentStatusList
    );
    @Query("SELECT p FROM PurchasedPlan p WHERE p.account = :account AND p.status = :status AND p.planPackage.coachSupport = :coachSupport")
    Optional<PurchasedPlan> findFirstByAccountAndStatusAndCoachSupport(
            @Param("account") Account account,
            @Param("status") com.quitsmoking.platform.enums.PlanStatus status,
            @Param("coachSupport") Boolean coachSupport
    );
    @Query("SELECT COUNT(DISTINCT p.account.id) FROM PurchasedPlan p WHERE p.status = :status")
    long countActiveUser(@Param("status") PlanStatus status);

    @Query("SELECT COUNT(DISTINCT p.account.id) FROM PurchasedPlan p")
    long countUserHasPurchasedPlan();



}
