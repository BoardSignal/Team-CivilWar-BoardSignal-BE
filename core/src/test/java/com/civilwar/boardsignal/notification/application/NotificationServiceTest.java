package com.civilwar.boardsignal.notification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import com.civilwar.boardsignal.notification.domain.entity.Notification;
import com.civilwar.boardsignal.notification.domain.repository.NotificationRepository;
import com.civilwar.boardsignal.notification.dto.response.NotificationPageResponse;
import com.civilwar.boardsignal.notification.dto.response.NotificationResponse;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@DisplayName("[NotificationService 테스트]")
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("[자신의 알림 목록을 조회할 수 있다.]")
    void getAllNotifications() {
        User user = UserFixture.getUserFixture("prpr", "https");
        Notification notification1 = Notification.of(
            user,
            "https",
            "제목1",
            "내용1",
            1L
        );
        Notification notification2 = Notification.of(
            user,
            "https",
            "제목2",
            "내용2",
            null
        );
        PageRequest pageRequest = PageRequest.of(0, 5);

        given(notificationRepository.findAllByUser(user, pageRequest))
            .willReturn(new PageImpl<>(List.of(notification1, notification2)));

        NotificationPageResponse<NotificationResponse> resposne = notificationService.getAllNotifications(
            user, pageRequest);

        List<NotificationResponse> contents = resposne.notificationsInfos();

        assertAll(
            () -> assertThat(contents.get(0).title()).isEqualTo(notification1.getTitle()),
            () -> assertThat(contents.get(0).body()).isEqualTo(notification1.getBody()),
            () -> assertThat(contents.get(0).roomId()).isEqualTo(notification1.getRoomID()),
            () -> assertThat(contents.get(1).title()).isEqualTo(notification2.getTitle()),
            () -> assertThat(contents.get(1).body()).isEqualTo(notification2.getBody()),
            () -> assertThat(contents.get(1).roomId()).isEqualTo(notification2.getRoomID())
        );
    }

}