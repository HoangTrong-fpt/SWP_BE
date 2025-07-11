package com.quitsmoking.platform.repository;


import com.quitsmoking.platform.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findBySentFalseAndSendAtBefore(ZonedDateTime now);
}

