package com.quitsmoking.platform.api;

import com.quitsmoking.platform.dto.NotificationRequest;
import com.quitsmoking.platform.dto.NotificationResponse;
import com.quitsmoking.platform.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.quitsmoking.platform.entity.Account;

@RestController
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "api")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NotificationRequest request, @AuthenticationPrincipal Account sender) {
        request.setSenderId(sender.getId());
        notificationService.createNotifications(request);
        return ResponseEntity.status(201).body("Notifications sent successfully");
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getByRecipient(@AuthenticationPrincipal Account user) {
        return ResponseEntity.ok(notificationService.getNotificationsByRecipient(user.getId()));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id, @AuthenticationPrincipal Account user) {
        return ResponseEntity.ok(notificationService.markAsRead(id, user.getId()));
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@AuthenticationPrincipal Account user) {
        int count = notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok("Đã cập nhật " + count + " thông báo.");
    }

    @PreAuthorize("hasAnyRole( 'ADMIN', 'COACH')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

