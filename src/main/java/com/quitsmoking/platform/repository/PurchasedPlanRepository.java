package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.PaymentStatus;
import com.quitsmoking.platform.enums.PlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.quitsmoking.platform.entity.Package;

import java.util.List;
import java.util.Optional;

public interface PurchasedPlanRepository extends JpaRepository<PurchasedPlan, Long> {
    List<PurchasedPlan> findByAccount(Account account);
    Optional<PurchasedPlan> findFirstByAccountAndStatus(Account account, PlanStatus status);
    Optional<PurchasedPlan> findByIdAndAccountAndUsedFalse(Long id, Account account);
    Optional<PurchasedPlan> findByAccountAndUsedFalse(Account account);
    Optional<PurchasedPlan> findFirstByAccountAndPlanPackageAndStatusInAndPaymentStatusIn(
            Account account,
            Package planPackage,
            List<PlanStatus> statusList,
            List<PaymentStatus> paymentStatusList
    );


}
