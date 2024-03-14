package com.civilwar.boardsignal.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record NotificationResponse(
    Long notificationId,
    String title,
    String body,
    String imageUrl,
    Long roomId,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdAt
) {

}
