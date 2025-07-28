package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.NotificationRequest;
import com.quitsmoking.platform.dto.NotificationResponse;
import com.quitsmoking.platform.dto.BulkNotificationResponse;
import com.quitsmoking.platform.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.quitsmoking.platform.entity.Account;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "api")
@Tag(name = "Notification", description = "Notification management APIs")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    @PostMapping
    @Operation(summary = "Gửi notification cho danh sách user cụ thể",
            description = "Admin/Coach có thể gửi notification cho một hoặc nhiều user")
    public ResponseEntity<?> create(@RequestBody NotificationRequest request, @AuthenticationPrincipal Account sender) {
        request.setSenderId(sender.getId());
        notificationService.createNotifications(request);
        return ResponseEntity.status(201).body("Notifications sent successfully");
    }

    // Thêm API mới cho admin gửi notification cho tất cả CUSTOMER
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/send-to-all-customers")
    @Operation(summary = "Gửi notification cho tất cả CUSTOMER",
            description = "Admin gửi notification cho tất cả user có role CUSTOMER đang hoạt động")
    public ResponseEntity<BulkNotificationResponse> sendToAllCustomers(@RequestBody NotificationRequest request, @AuthenticationPrincipal Account sender) {
        request.setSenderId(sender.getId());
        int sentCount = notificationService.sendNotificationToAllCustomers(request);

        BulkNotificationResponse response = new BulkNotificationResponse(
                "Đã gửi thông báo cho tất cả customer thành công",
                sentCount,
                LocalDateTime.now(),
                request.getTitle(),
                request.getType()
        );

        return ResponseEntity.status(201).body(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    @Operation(summary = "Lấy danh sách notification của user",
            description = "Customer xem tất cả notification của mình")
    public ResponseEntity<List<NotificationResponse>> getByRecipient(@AuthenticationPrincipal Account user) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(user.getId()));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/{id}/read")
    @Operation(summary = "Đánh dấu notification đã đọc",
            description = "Customer đánh dấu một notification đã đọc")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id, @AuthenticationPrincipal Account user) {
        return ResponseEntity.ok(notificationService.markAsRead(id, user.getId()));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/read-all")
    @Operation(summary = "Đánh dấu tất cả notification đã đọc",
            description = "Customer đánh dấu tất cả notification đã đọc")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal Account user) {
        int count = notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok("Đã cập nhật " + count + " thông báo.");
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/unread-count")
    @Operation(summary = "Đếm số notification chưa đọc",
            description = "Customer xem số lượng notification chưa đọc")
    public ResponseEntity<Integer> getUnreadCount(@AuthenticationPrincipal Account user) {
        int count = notificationService.countUnreadByRecipient(user.getId());
        return ResponseEntity.ok(count);
    }

    @PreAuthorize("hasAnyRole( 'ADMIN', 'COACH')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa notification",
            description = "Admin/Coach xóa một notification")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Thêm API để admin xem thống kê notification
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    @Operation(summary = "Xem thống kê notification",
            description = "Admin xem thống kê tổng quan về notification trong hệ thống")
    public ResponseEntity<NotificationService.NotificationStats> getNotificationStats() {
        NotificationService.NotificationStats stats = notificationService.getNotificationStats();
        return ResponseEntity.ok(stats);
    }
}

