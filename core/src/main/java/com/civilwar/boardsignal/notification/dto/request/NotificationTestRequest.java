package com.civilwar.boardsignal.notification.dto.request;

public record NotificationTestRequest(
    String targetToken,
    String title,
    String body,
    String imageUrl
) {

}
