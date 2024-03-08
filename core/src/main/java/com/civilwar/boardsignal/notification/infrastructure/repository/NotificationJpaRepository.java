package com.civilwar.boardsignal.notification.infrastructure.repository;

import com.civilwar.boardsignal.notification.domain.entity.Notification;
import com.civilwar.boardsignal.user.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    Slice<Notification> findAllByUser(User user, Pageable pageable);
}
