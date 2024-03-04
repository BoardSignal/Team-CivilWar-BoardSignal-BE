package com.civilwar.boardsignal.notification.event;

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

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMessage(Notification notification) {
        Notification savedNotification = notificationService.saveNotification(notification);
        notificationService.sendMessage(savedNotification);
    }
}
