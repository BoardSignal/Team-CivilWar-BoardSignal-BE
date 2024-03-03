package com.civilwar.boardsignal.notification.infrastructure.repository;

import com.civilwar.boardsignal.notification.domain.constant.NotificationStatus;
import com.civilwar.boardsignal.notification.domain.entity.Notification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByStatus(NotificationStatus status);
}
