package com.civilwar.boardsignal.notification.dto.request;

import java.util.List;

public record NotificationRequest(
    String title,
    String body,
    String imageUrl,
    Long roomId,
    List<Long> userIds
) {

}
