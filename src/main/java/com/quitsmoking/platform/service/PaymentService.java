package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.PaymentConfirmRequest;
import com.quitsmoking.platform.entity.Payment;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.PaymentStatus;
import com.quitsmoking.platform.enums.PlanStatus;
import com.quitsmoking.platform.repository.PaymentRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private PurchasedPlanRepository purchasedPlanRepo;

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.payUrl}")
    private String vnp_PayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    // Tạo mới Payment và build URL cho VNPay
    public String createPaymentAndBuildVNPayUrl(PurchasedPlan plan, double amount, String description, String clientIp) {
        Payment payment = new Payment();
        payment.setPurchasedPlan(plan);
        payment.setAmount(amount);
        payment.setDescription(description);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);

        String transactionId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        payment.setTransactionId(transactionId);

        paymentRepository.save(payment);

        String paymentUrl = createVNPayUrl(payment, clientIp);
        payment.setPaymentUrl(paymentUrl);
        paymentRepository.save(payment);

        return paymentUrl;
    }


    // Build URL VNPay dựa trên Payment
    public String createVNPayUrl(Payment payment, String clientIp) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String createDate = LocalDateTime.now().format(formatter);

            TreeMap<String, String> vnpParams = new TreeMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", vnp_TmnCode.trim());
            vnpParams.put("vnp_Amount", String.valueOf((long)(payment.getAmount() * 100)));
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", payment.getTransactionId());
            vnpParams.put("vnp_OrderInfo", payment.getDescription());
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            // SỬA Ở ĐÂY:
            String returnUrlWithIds = vnp_ReturnUrl.trim()
                    + (vnp_ReturnUrl.contains("?") ? "&" : "?")
                    + "paymentId=" + payment.getId()
                    + "&planId=" + payment.getPurchasedPlan().getId();
            vnpParams.put("vnp_ReturnUrl", returnUrlWithIds);

            vnpParams.put("vnp_CreateDate", createDate);
            vnpParams.put("vnp_IpAddr", clientIp);

            // Build data to hash
            StringBuilder signDataBuilder = new StringBuilder();
            for (var entry : vnpParams.entrySet()) {
                signDataBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()))
                        .append("&");
            }
            signDataBuilder.deleteCharAt(signDataBuilder.length() - 1);

            String signData = signDataBuilder.toString();
            String secureHash = hmacSHA512(vnp_HashSecret.trim(), signData);

            StringBuilder urlBuilder = new StringBuilder(vnp_PayUrl).append("?");
            for (var entry : vnpParams.entrySet()) {
                urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()))
                        .append("&");
            }
            urlBuilder.append("vnp_SecureHash=").append(secureHash);

            return urlBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo VNPay URL", e);
        }
    }


    // Xác nhận thanh toán (FE gọi)
    public void confirmPayment(PaymentConfirmRequest req, String username) {
        Payment payment = paymentRepository.findById(req.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy payment!"));
        PurchasedPlan plan = purchasedPlanRepo.findById(req.getPlanId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy plan!"));

        if (!plan.getAccount().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không sở hữu plan này!");
        }

        // Cập nhật trạng thái
        if ("SUCCESS".equalsIgnoreCase(req.getPaymentStatus())) {
            payment.setStatus(PaymentStatus.SUCCESS);
            plan.setPaymentStatus(PaymentStatus.SUCCESS);
            plan.setStatus(PlanStatus.PENDING); // Không active ngay!
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            plan.setPaymentStatus(PaymentStatus.FAILED);
            plan.setStatus(PlanStatus.CANCELED);
        }
        payment.setCompletedAt(LocalDateTime.now());

        paymentRepository.save(payment);
        purchasedPlanRepo.save(plan);
    }

    private String hmacSHA512(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac.init(secretKey);
        byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}

