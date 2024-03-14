package com.civilwar.boardsignal.notification.dto.mapper;

import com.civilwar.boardsignal.notification.domain.entity.Notification;
import com.civilwar.boardsignal.notification.dto.response.NotificationPageResponse;
import com.civilwar.boardsignal.notification.dto.response.NotificationResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationMapper {

    public static NotificationResponse toNotificationResponse(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getTitle(),
            notification.getBody(),
            notification.getImageUrl(),
            notification.getRoomID(),
            notification.getCreatedAt()
        );
    }

    public static <T> NotificationPageResponse<T> toNotificationPageResponse(Slice<T> page) {
        return new NotificationPageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.hasNext()
        );
    }

}
