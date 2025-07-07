package com.quitsmoking.platform.api;

import com.quitsmoking.platform.entity.Payment;
import com.quitsmoking.platform.repository.PaymentRepository;
import com.quitsmoking.platform.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment")
public class PaymentAPI {
    @Autowired private PaymentService paymentService;
    @Autowired private PaymentRepository paymentRepository;

    // VNPay callback (public, không cần JWT)
    @GetMapping("/vnpay-callback")
    public ResponseEntity<String> vnpayCallback(HttpServletRequest request) {
        try {
            boolean valid = paymentService.processVnpayCallback(request);
            return ResponseEntity.ok(valid ? "OK" : "FAILED");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("ERROR");
        }
    }

    // API cho FE check trạng thái giao dịch
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/status")
    public ResponseEntity<?> getPaymentStatus(@RequestParam String txnRef) {
        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(txnRef);
        if (!paymentOpt.isPresent()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(paymentOpt.get().getStatus());
    }
}


