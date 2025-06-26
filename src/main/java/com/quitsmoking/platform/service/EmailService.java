package com.quitsmoking.platform.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // Gửi email đăng ký thành công
    public void sendRegisterMail(String to, String fullName, String link, String buttonText) {
        Context context = new Context();
        context.setVariable("name", fullName);
        context.setVariable("link", link);
        context.setVariable("button", buttonText);

        String html = templateEngine.process("register_template", context);
        sendHtml(to, "Chào mừng bạn đến với quitsmoke.fun", html);
    }

    // Gửi email OTP quên mật khẩu
    public void sendOtpMail(String to, String fullName, String otp, int expiryMinutes) {
        Context context = new Context();
        context.setVariable("name", fullName);
        context.setVariable("otp", otp);
        context.setVariable("expiryMinutes", expiryMinutes);

        String html = templateEngine.process("otp_email", context);
        sendHtml(to, "Mã OTP đặt lại mật khẩu", html);
    }

    // Hàm gửi HTML mail dùng chung cho các loại mail
    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println("Lỗi gửi mail: " + e.getMessage());
        }
    }

}
