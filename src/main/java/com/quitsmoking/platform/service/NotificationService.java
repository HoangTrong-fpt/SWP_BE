package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.NotificationRequest;
import com.quitsmoking.platform.dto.NotificationResponse;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Notification;
import com.quitsmoking.platform.repository.AccountRepository;
import com.quitsmoking.platform.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final AccountRepository accountRepo;
    @Autowired
    public NotificationService(NotificationRepository notificationRepo, AccountRepository accountRepo) {
        this.notificationRepo = notificationRepo;
        this.accountRepo = accountRepo;

    }


    public NotificationResponse create(NotificationRequest request) {
        Account account = accountRepo.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Notification notification = new Notification();
        notification.setAccount(account);
        notification.setSubject(request.getSubject());
        notification.setContent(request.getContent());
        notification.setSendAt(request.getSendAt());
        notification.setSent(false);

        notificationRepo.save(notification);

        return new NotificationResponse(account.getEmail(), request.getSubject(), request.getContent(), request.getSendAt(), false);
    }

    public List<NotificationResponse> getAll() {
        return notificationRepo.findAll().stream()
                .map(n -> new NotificationResponse(
                        n.getAccount().getEmail(),
                        n.getSubject(),
                        n.getContent(),
                        n.getSendAt(),
                        n.isSent()
                )).toList();
    }

    public void sendDueNotifications() {
        ZonedDateTime now = ZonedDateTime.now();
        List<Notification> dueNotifications = notificationRepo.findBySentFalseAndSendAtBefore(now);

        for (Notification n : dueNotifications) {
            // TODO: Send email using MailService
            n.setSent(true);
            notificationRepo.save(n);
        }
    }

    public void delete(Long id) {
        notificationRepo.deleteById(id);
    }
}

