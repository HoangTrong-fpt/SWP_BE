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
            Account recipient = accountRepository.findById(request.getRecipientIds().get(0))
                    .orElseThrow(() -> new RuntimeException("Recipient not found"));

            Notification notification = new Notification();
            notification.setRecipient(recipient);
            notification.setSender(accountRepository.findById(request.getSenderId()).orElse(null));
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setType(request.getType());
            notification.setIsRead(false);
            notification.setCreatedAt(java.time.LocalDateTime.now());
            notification = notificationRepository.save(notification);
            return toResponse(notification);
        } else {
            // Nếu nhiều recipient, chỉ trả về notification đầu tiên (có thể trả về null tuỳ ý)
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

        Account sender = accountRepository.findById(request.getSenderId()).orElse(null);
        List<Notification> notifications = targetIds.stream().map(recipientId -> {
            Account recipient = accountRepository.findById(recipientId)
                    .orElseThrow(() -> new RuntimeException("Recipient not found: " + recipientId));

            Notification n = new Notification();
            n.setRecipient(recipient);
            n.setSender(sender);
            n.setTitle(request.getTitle());
            n.setMessage(request.getMessage());
            n.setType(request.getType());
            n.setIsRead(false);
            n.setCreatedAt(java.time.LocalDateTime.now());
            return n;
        }).toList();
        notificationRepository.saveAll(notifications);
    }

    public int sendNotificationToAllCustomers(NotificationRequest request) {

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Tiêu đề thông báo không được để trống");
        }

        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new RuntimeException("Nội dung thông báo không được để trống");
        }

        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new RuntimeException("Loại thông báo không được để trống");
        }

        // Lấy tất cả account có role CUSTOMER và đang active
        List<Account> customers = accountRepository.findAll().stream()
                .filter(acc -> acc.getRole() != null &&
                        acc.getRole().name().equals("CUSTOMER") &&
                        Boolean.TRUE.equals(acc.getActive()))
                .toList();

        if (customers.isEmpty()) {
            throw new RuntimeException("Không có customer nào đang hoạt động trong hệ thống");
        }

        Account sender = accountRepository.findById(request.getSenderId()).orElse(null);

        // Tạo notification cho từng customer
        List<Notification> notifications = customers.stream().map(customer -> {
            Notification n = new Notification();
            n.setRecipient(customer);
            n.setSender(sender);
            n.setTitle(request.getTitle().trim());
            n.setMessage(request.getMessage().trim());
            n.setType(request.getType().trim());
            n.setIsRead(false);
            n.setCreatedAt(LocalDateTime.now());
            return n;
        }).toList();


        notificationRepository.saveAll(notifications);

        return notifications.size();
    }

    public List<NotificationResponse> getNotificationsByRecipient(Long recipientId) {
        Account recipient = accountRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        return notificationRepository.findByRecipient(recipient)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public NotificationResponse markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền đánh dấu thông báo này.");
        }
        notification.setIsRead(true);
        notification = notificationRepository.save(notification);
        return toResponse(notification);
    }

    public int markAllAsRead(Long userId) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Notification> notis = notificationRepository.findByRecipient(user)
                .stream().filter(n -> !Boolean.TRUE.equals(n.getIsRead())).toList();
        notis.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notis);
        return notis.size();
    }

    public int countUnreadByRecipient(Long recipientId) {
        Account recipient = accountRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        return notificationRepository.countByRecipientAndIsReadFalse(recipient);
    }

    public void delete(Long id) {
        notificationRepository.deleteById(id);
    }

    // Thêm method để lấy thống kê notification
    public NotificationStats getNotificationStats() {
        long totalNotifications = notificationRepository.count();

        // Tính số notification chưa đọc
        long unreadNotifications = notificationRepository.findAll().stream()
                .filter(n -> !Boolean.TRUE.equals(n.getIsRead()))
                .count();

        long readNotifications = totalNotifications - unreadNotifications;

        return new NotificationStats(totalNotifications, readNotifications, unreadNotifications);
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse res = new NotificationResponse();
        res.setId(n.getId());
        res.setRecipientId(n.getRecipient().getId());
        res.setSenderId(n.getSender() != null ? n.getSender().getId() : null);
        res.setTitle(n.getTitle());
        res.setMessage(n.getMessage());
        res.setType(n.getType());
        res.setIsRead(n.getIsRead());
        res.setCreatedAt(n.getCreatedAt());
        return res;
    }

    // Inner class cho thống kê
    public static class NotificationStats {
        private long totalNotifications;
        private long readNotifications;
        private long unreadNotifications;

        public NotificationStats(long totalNotifications, long readNotifications, long unreadNotifications) {
            this.totalNotifications = totalNotifications;
            this.readNotifications = readNotifications;
            this.unreadNotifications = unreadNotifications;
        }

        // Getters
        public long getTotalNotifications() { return totalNotifications; }
        public long getReadNotifications() { return readNotifications; }
        public long getUnreadNotifications() { return unreadNotifications; }
    }
}

