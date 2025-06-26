package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.MailBody;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.ForgotPassword;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.ForgotPasswordRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;

@Service
@Transactional
public class ForgotPasswordService {
    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Integer generateOtp() {
        return new Random().nextInt(900_000) + 100_000;
    }

    public void sendOtp(String email) {
        Account account = authenticationRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        Integer otp = generateOtp();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .subject("OTP for Forgot Password")
                .text("This is your OTP: " + otp)
                .build();
        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(Instant.now().plusSeconds(5 * 60))
                .account(account)
                .build();
        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(forgotPassword);
    }

    public ForgotPassword verifyOtp(String email, Integer otp) {
        Account account = authenticationRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        ForgotPassword forgotPassword = forgotPasswordRepository.findByOtpAndAccount(otp, account)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));
        if (forgotPassword.getExpirationTime().isBefore(Instant.now())) {
            forgotPasswordRepository.deleteById(forgotPassword.getId());
            throw new RuntimeException("OTP expired");
        }
        return forgotPassword;
    }

    public void resetPassword(String email, Integer otp, String newPassword) {
        ForgotPassword forgotPassword = verifyOtp(email, otp);
        String encoded = passwordEncoder.encode(newPassword);
        authenticationRepository.updatePassword(email, encoded);
        forgotPasswordRepository.deleteById(forgotPassword.getId());
    }
}
