package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.PaymentRequest;
import com.quitsmoking.platform.entity.Payment;
import com.quitsmoking.platform.entity.PurchasedPlan;
import com.quitsmoking.platform.enums.PaymentStatus;
import com.quitsmoking.platform.repository.PaymentRepository;
import com.quitsmoking.platform.repository.PurchasedPlanRepository;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PurchasedPlanRepository purchasedPlanRepo;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.payUrl}")
    private String vnp_PayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    public String createPayment(PaymentRequest request, String clientIp)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        // Lấy plan object (KHÔNG dùng id trực tiếp nữa)
        PurchasedPlan plan = purchasedPlanRepo.findById(request.getPurchasedPlanId())
                .orElseThrow(() -> new RuntimeException("PurchasedPlan not found"));

        Payment payment = new Payment();
        payment.setAmount(request.getAmount());
        payment.setDescription(request.getDescription());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPurchasedPlan(plan); // <-- Gán object plan luôn

        String transactionId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        payment.setTransactionId(transactionId);

        paymentRepository.save(payment);

        String paymentUrl = createVNPayUrl(payment, clientIp);
        payment.setPaymentUrl(paymentUrl);
        paymentRepository.save(payment);

        return paymentUrl;
    }


    private String createVNPayUrl(Payment payment, String clientIp)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String createDate = LocalDateTime.now().format(formatter);

        // Build param map (sort)
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnp_TmnCode.trim());
        vnpParams.put("vnp_Amount", String.valueOf(payment.getAmount().longValue() * 100));
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", payment.getTransactionId());
        vnpParams.put("vnp_OrderInfo", payment.getDescription()); // không dấu
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnp_ReturnUrl.trim());
        vnpParams.put("vnp_IpAddr", clientIp);
        vnpParams.put("vnp_CreateDate", createDate);

        // 1. Build signData: encode từng key và value!
        String signData = buildSignData(vnpParams);

        // 2. Sinh SecureHash
        String secureHash = hmacSHA512(vnp_HashSecret.trim(), signData);

        // 3. Build URL: cũng encode từng key-value
        String queryUrl = buildQueryUrl(vnpParams);
        queryUrl += "&vnp_SecureHash=" + secureHash;

        // Debug log nếu cần
        System.out.println("signData: " + signData);
        System.out.println("secureHash: " + secureHash);

        return vnp_PayUrl + "?" + queryUrl;
    }

    // Hàm build signData (chuẩn mới: encode từng key và value)
    private String buildSignData(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1); // xóa ký tự '&' cuối
        return sb.toString();
    }

    // Hàm build query URL (giống build signData)
    private String buildQueryUrl(Map<String, String> params) throws UnsupportedEncodingException {
        return buildSignData(params); // cùng format với signData
    }

    // SecureHash
    private String hmacSHA512(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac.init(secretKey);
        byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public Payment processCallback(HttpServletRequest request) {
        String txnRef = request.getParameter("vnp_TxnRef");
        String responseCode = request.getParameter("vnp_ResponseCode");

        Payment payment = paymentRepository.findByTransactionId(txnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        payment.setCompletedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public boolean processVnpayCallback(HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeyException {
        // 1. Parse tất cả params
        Map<String, String> params = new HashMap<>();
        for (Enumeration<String> en = request.getParameterNames(); en.hasMoreElements();) {
            String key = en.nextElement();
            params.put(key, request.getParameter(key));
        }

        // 2. Lấy hash và bỏ khỏi param (KHÔNG đưa vào signData)
        String receivedHash = params.remove("vnp_SecureHash");
        String receivedHashType = params.remove("vnp_SecureHashType"); // Nếu có

        // 3. Build lại signData
        String signData = params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        // 4. Tính lại hash
        String calculatedHash = hmacSHA512(vnp_HashSecret, signData);

        // 5. So sánh (không phân biệt hoa thường)
        if (!calculatedHash.equalsIgnoreCase(receivedHash)) {
            return false;
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(txnRef);
        if (!paymentOpt.isPresent()) return false;
        Payment payment = paymentOpt.get();

        // Update trạng thái Payment
        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.SUCCESS);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        // ---- UPDATE PurchasedPlan ----
        if (payment.getPurchasedPlan() != null) {
            PurchasedPlan plan = payment.getPurchasedPlan();
            if ("00".equals(responseCode)) {
                plan.setPaymentStatus(PaymentStatus.SUCCESS);
                plan.setStatus(com.quitsmoking.platform.enums.PlanStatus.PENDING); // hoặc ACTIVE nếu muốn kích hoạt luôn
            } else {
                plan.setPaymentStatus(PaymentStatus.FAILED);
                plan.setStatus(com.quitsmoking.platform.enums.PlanStatus.CANCELED);
            }
            // Inject repo vào service!
            purchasedPlanRepo.save(plan);
        }
        // ------------------------------

        return true;
    }



}
