package com.civilwar.boardsignal.notification.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.notification.domain.entity.Notification;
import com.civilwar.boardsignal.notification.domain.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class NotificationControllerTest extends ApiTestSupport {

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification notificationFirst;
    private Notification notificationSecond;

    @BeforeEach
    void setUp() {
        Notification notification1 = Notification.of(
            loginUser,
            "https~",
            "제목1",
            "내용1",
            1L
        );
        Notification notification2 = Notification.of(
            loginUser,
            "https~",
            "제목2",
            "내용2",
            null
        );

        notificationFirst = notificationRepository.save(notification1);
        notificationSecond = notificationRepository.save(notification2);
    }

    @Test
    @DisplayName("[자신의 알림 목록을 전체 조회할 수 있다.]")
    void getAllNotifications() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("size", "10");

        mockMvc.perform(get("/api/v1/notifications/my")
                .params(params)
                .header(AUTHORIZATION, accessToken))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.notificationsInfos[0].notificationId").value(notificationFirst.getId()),
                jsonPath("$.notificationsInfos[0].roomId").value(notificationFirst.getRoomID()),
                jsonPath("$.notificationsInfos[1].notificationId").value(
                    notificationSecond.getId()),
                jsonPath("$.notificationsInfos[1].roomId").value(notificationSecond.getRoomID()),
                jsonPath("$.size").value(10),
                jsonPath("$.hasNext").value(false)
            );

    }
}