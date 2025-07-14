package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientId(Long recipientId);
    int countByRecipientIdAndIsReadFalse(Long recipientId);
}

