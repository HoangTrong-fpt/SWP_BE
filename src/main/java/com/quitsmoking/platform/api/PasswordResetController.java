package com.quitsmoking.platform.api;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.PasswordResetToken;
import com.quitsmoking.platform.repository.AccountRepository;
import com.quitsmoking.platform.repository.PasswordResetTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/password")
public class PasswordResetController {

    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordResetTokenRepository tokenRepository;
    @Autowired private JavaMailSender mailSender;

    @PostMapping("/forgot")
    public String processForgotPassword(@RequestParam String email) {
        Optional<Account> accountOpt = accountRepository.findByEmail(email);
        if (accountOpt.isEmpty()) return "Email không tồn tại";

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(email, token, LocalDateTime.now().plusMinutes(30));
        tokenRepository.save(resetToken);

        String link = "http://localhost:8080/reset-password?token=" + token;
        try {
            sendEmail(email, link);
            return "Đã gửi email đặt lại mật khẩu.";
        } catch (MessagingException e) {
            return "Gửi email thất bại.";
        }
    }

    @PostMapping("/reset")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty() || tokenOpt.get().getExpiry().isBefore(LocalDateTime.now())) {
            return "Token không hợp lệ hoặc đã hết hạn.";
        }

        Optional<Account> accOpt = accountRepository.findByEmail(tokenOpt.get().getEmail());
        if (accOpt.isPresent()) {
            Account account = accOpt.get();
            account.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            accountRepository.save(account);
            tokenRepository.delete(tokenOpt.get());
            return "Đặt lại mật khẩu thành công.";
        }

        return "Không tìm thấy tài khoản.";
    }

    private void sendEmail(String to, String link) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Khôi phục mật khẩu");
        helper.setText("Nhấn vào liên kết sau để đặt lại mật khẩu: " + link, true);
        mailSender.send(message);
    }
}
