package com.civilwar.boardsignal.room.facade;

import static org.assertj.core.api.Assertions.assertThat;

import com.civilwar.boardsignal.common.MultipartFileFixture;
import com.civilwar.boardsignal.notification.dto.request.NotificationRequest;
import com.civilwar.boardsignal.notification.event.NotificationEventHandler;
import com.civilwar.boardsignal.room.RoomFixture;
import com.civilwar.boardsignal.room.dto.request.CreateRoomRequest;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@SpringBootTest
@RecordApplicationEvents
class RoomFacadeTest {

    @Autowired
    private RoomFacade roomFacade;

    @Autowired
    private ApplicationEvents events;

    @Autowired
    private NotificationEventHandler eventHandler;

    @Test
    @DisplayName("[방 생성 시 해당 지하철 역에 대한 유저들에게 알림 이벤트가 발생한다.]")
    void createRoom() throws IOException {
        //given
        User user = UserFixture.getUserFixture("providerID", "imageUrl");

        MockMultipartFile imageFixture = MultipartFileFixture.getMultipartFile();
        CreateRoomRequest request = RoomFixture.getCreateRoomRequest(imageFixture);

        //when
        roomFacade.createRoom(user, request);
        int count = (int) events.stream(NotificationRequest.class).count();

        //then
        assertThat(count).isEqualTo(1);
    }

}