    package com.quitsmoking.platform.api;

    import com.quitsmoking.platform.dto.PaymentConfirmRequest;
    import com.quitsmoking.platform.dto.PaymentRequest;
    import com.quitsmoking.platform.entity.Payment;
    import com.quitsmoking.platform.repository.PaymentRepository;
    import com.quitsmoking.platform.service.PaymentService;
    import io.swagger.v3.oas.annotations.security.SecurityRequirement;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import jakarta.servlet.http.HttpServletRequest;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.security.core.Authentication;
    import org.springframework.web.bind.annotation.*;

    import java.util.Map;
    import java.util.Optional;


    @RestController
    @RequestMapping("/api/payment")
    @SecurityRequirement(name = "api")
    @Tag(name = "Payment")
    public class PaymentAPI {
        @Autowired
        private PaymentService paymentService;

        @PostMapping("/confirm")
        public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmRequest req, Authentication auth) {
            try {
                paymentService.confirmPayment(req, auth.getName());

                if ("SUCCESS".equalsIgnoreCase(req.getPaymentStatus())) {
                    return ResponseEntity.ok(Map.of(
                            "paymentStatus", "SUCCESS",
                            "message", "Thanh toán thành công"
                    ));
                } else {
                    return ResponseEntity.ok(Map.of(
                            "paymentStatus", "FAILED",
                            "message", "Thanh toán bị hủy hoặc thất bại"
                    ));
                }
            } catch (Exception ex) {
                return ResponseEntity.status(400).body(Map.of(
                        "paymentStatus", "FAILED",
                        "message", ex.getMessage()
                ));
            }
        }


    }