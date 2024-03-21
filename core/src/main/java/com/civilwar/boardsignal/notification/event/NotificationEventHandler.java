package com.civilwar.boardsignal.notification.event;

import static com.civilwar.boardsignal.user.exception.UserErrorCode.NOT_FOUND_USER;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.notification.application.FcmSender;
import com.civilwar.boardsignal.notification.application.NotificationService;
import com.civilwar.boardsignal.notification.domain.entity.Notification;
import com.civilwar.boardsignal.notification.dto.request.NotificationRequest;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.entity.UserFcmToken;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final FcmSender fcmSender; // 외부 api 연동 서비스
    private final NotificationService notificationService; // 알림 레포지토리 의존 서비스
    private final UserRepository userRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMessage(NotificationRequest request) {
        List<User> users = userRepository.findAllInIds(request.userIds());
        for (User user : users) {
            Notification notification = Notification.of(
                user,
                request.imageUrl(),
                request.title(),
                request.body(),
                request.roomId()
            );
            Notification savedNotification = notificationService.saveNotification(notification);
            User findUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_USER));

            findUser.getUserFcmTokens().stream()
                .map(UserFcmToken::getToken)
                .forEach(token -> fcmSender.sendMessage(token, savedNotification));
        }
    }
}
