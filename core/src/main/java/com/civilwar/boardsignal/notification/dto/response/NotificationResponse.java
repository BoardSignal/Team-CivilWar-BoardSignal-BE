package com.civilwar.boardsignal.notification.dto.response;

public record NotificationResponse(
    Long notificationId,
    String title,
    String body,
    String imageUrl,
    Long roomId
) {

}
