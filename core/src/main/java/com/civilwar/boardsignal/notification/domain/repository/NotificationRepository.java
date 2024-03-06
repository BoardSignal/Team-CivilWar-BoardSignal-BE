package com.civilwar.boardsignal.notification.domain.repository;

import com.civilwar.boardsignal.notification.domain.constant.NotificationStatus;
import com.civilwar.boardsignal.notification.domain.entity.Notification;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationRepository {

    Optional<Notification> findById(Long id);

    Optional<Notification> findByStatus(NotificationStatus status);

    Slice<Notification> findAll(Pageable pageable);

    Notification save(Notification notification);

    Slice<Notification> findAllByUser(User user, Pageable pageable);

}
