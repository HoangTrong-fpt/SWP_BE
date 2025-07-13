package com.quitsmoking.platform.service;

import com.quitsmoking.platform.dto.NotificationRequest;
import com.quitsmoking.platform.dto.NotificationResponse;
import com.quitsmoking.platform.entity.Notification;
import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.repository.NotificationRepository;
import com.quitsmoking.platform.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private AccountRepository accountRepository;

    public NotificationResponse createNotification(NotificationRequest request) {
        if (request.getRecipientIds() == null || request.getRecipientIds().isEmpty()) {
            throw new RuntimeException("recipientIds is required");
        }
        if (request.getRecipientIds().size() == 1) {
            Notification notification = new Notification();
            notification.setRecipientId(request.getRecipientIds().get(0));
            notification.setSenderId(request.getSenderId());
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setType(request.getType());
            notification.setIsRead(false);
            notification.setCreatedAt(java.time.LocalDateTime.now());
            notification = notificationRepository.save(notification);
            return toResponse(notification);
        } else {
            // Nếu nhiều recipient, chỉ trả về notification đầu tiên (hoặc có thể trả về null tuỳ ý)
            createNotifications(request);
            return null;
        }
    }

    public void createNotifications(NotificationRequest request) {
        List<Long> targetIds = request.getRecipientIds();
        if (targetIds.size() == 1 && targetIds.get(0) == -1L) { // -1L đại diện cho "all"
            targetIds = accountRepository.findAll().stream()
                .filter(acc -> acc.getRole() != null && acc.getRole().name().equals("CUSTOMER"))
                .map(Account::getId).toList();
        }
        List<Notification> notifications = targetIds.stream().map(recipientId -> {
            Notification n = new Notification();
            n.setRecipientId(recipientId);
            n.setSenderId(request.getSenderId());
            n.setTitle(request.getTitle());
            n.setMessage(request.getMessage());
            n.setType(request.getType());
            n.setIsRead(false);
            n.setCreatedAt(java.time.LocalDateTime.now());
            return n;
        }).toList();
        notificationRepository.saveAll(notifications);
    }

    public List<NotificationResponse> getNotificationsByRecipient(Long recipientId) {
        return notificationRepository.findByRecipientId(recipientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public NotificationResponse markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!notification.getRecipientId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền đánh dấu thông báo này.");
        }
        notification.setIsRead(true);
        notification = notificationRepository.save(notification);
        return toResponse(notification);
    }

    public int markAllAsRead(Long userId) {
        List<Notification> notis = notificationRepository.findByRecipientId(userId)
            .stream().filter(n -> !Boolean.TRUE.equals(n.getIsRead())).toList();
        notis.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notis);
        return notis.size();
    }

    public void delete(Long id) {
        notificationRepository.deleteById(id);
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse res = new NotificationResponse();
        res.setId(n.getId());
        res.setRecipientId(n.getRecipientId());
        res.setSenderId(n.getSenderId());
        res.setTitle(n.getTitle());
        res.setMessage(n.getMessage());
        res.setType(n.getType());
        res.setIsRead(n.getIsRead());
        res.setCreatedAt(n.getCreatedAt());
        return res;
    }
}

