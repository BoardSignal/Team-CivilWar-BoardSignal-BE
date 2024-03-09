package com.civilwar.boardsignal.notification.dto.response;

import java.util.List;

public record NotificationPageResponse<T>(
    List<T> notificationsInfos,
    int size,
    boolean hasNext
) {

}
