package com.civilwar.boardsignal.notification.application;

import com.civilwar.boardsignal.notification.domain.entity.Notification;
import com.civilwar.boardsignal.notification.domain.repository.NotificationRepository;
import com.civilwar.boardsignal.notification.dto.mapper.NotificationMapper;
import com.civilwar.boardsignal.notification.dto.request.CreateFcmTokenReequest;
import com.civilwar.boardsignal.notification.dto.response.CreateFcmTokenResponse;
import com.civilwar.boardsignal.notification.dto.response.NotificationPageResponse;
import com.civilwar.boardsignal.notification.dto.response.NotificationResponse;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.entity.UserFcmToken;
import com.civilwar.boardsignal.user.domain.repository.UserFcmTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserFcmTokenRepository userFcmTokenRepository;

    @Transactional(readOnly = true)
    public NotificationPageResponse<NotificationResponse> getAllNotifications(User user,
        Pageable pageable) {
        Slice<NotificationResponse> notifications = notificationRepository
            .findAllByUser(user, pageable)
            .map(NotificationMapper::toNotificationResponse);

        return NotificationMapper.toNotificationPageResponse(notifications);
    }

    //사용자 기기 토큰 저장
    @Transactional
    public CreateFcmTokenResponse createFcmToken(User user, CreateFcmTokenReequest request) {
        UserFcmToken userFcmToken = UserFcmToken.of(user, request.token());
        UserFcmToken savedToken = userFcmTokenRepository.save(userFcmToken);
        return new CreateFcmTokenResponse(savedToken.getId());
    }

    @Transactional
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
}
