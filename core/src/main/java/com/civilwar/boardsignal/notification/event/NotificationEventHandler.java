package com.civilwar.boardsignal.notification.event;

import com.civilwar.boardsignal.notification.application.FcmService;
import com.civilwar.boardsignal.notification.application.NotificationService;
import com.civilwar.boardsignal.notification.domain.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final FcmService fcmService; // 외부 api 연동 서비스
    private final NotificationService notificationService; // 알림 레포지토리 의존 서비스

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMessage(Notification notification) {
        Notification savedNotification = notificationService.saveNotification(notification);
        fcmService.sendMessage(savedNotification);
    }
}
