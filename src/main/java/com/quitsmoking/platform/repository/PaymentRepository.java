package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Payment;
import com.quitsmoking.platform.entity.PurchasedPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByPurchasedPlan(PurchasedPlan plan);
}
