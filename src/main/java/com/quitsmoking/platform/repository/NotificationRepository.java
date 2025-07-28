package com.quitsmoking.platform.repository;

import com.quitsmoking.platform.entity.Notification;
import com.quitsmoking.platform.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipient(Account recipient);
    List<Notification> findBySender(Account sender);
    List<Notification> findByRecipientAndIsReadFalse(Account recipient);
    int countByRecipientAndIsReadFalse(Account recipient);
}

