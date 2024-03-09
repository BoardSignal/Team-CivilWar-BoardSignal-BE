package com.civilwar.boardsignal.notification.infrastructure.adaptor;

import com.civilwar.boardsignal.notification.domain.entity.Notification;
import com.civilwar.boardsignal.notification.domain.repository.NotificationRepository;
import com.civilwar.boardsignal.notification.infrastructure.repository.NotificationJpaRepository;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdaptor implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    @Override
    public Optional<Notification> findById(Long id) {
        return notificationJpaRepository.findById(id);
    }

    @Override
    public Slice<Notification> findAll(Pageable pageable) {
        return notificationJpaRepository.findAll(pageable);
    }

    @Override
    public Notification save(Notification notification) {
        return notificationJpaRepository.save(notification);
    }

    @Override
    public Slice<Notification> findAllByUser(User user, Pageable pageable) {
        return notificationJpaRepository.findAllByUser(user, pageable);
    }
}
