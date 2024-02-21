package com.civilwar.boardsignal.room.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.civilwar.boardsignal.common.MultipartFileFixture;
import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.room.RoomFixture;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.request.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.response.CreateRoomRequest;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("[RoomService 테스트]")
@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    @DisplayName("[방을 생성할 수 있다.]")
    void createRoom() throws IOException {
        MockMultipartFile imageFixture = MultipartFileFixture.getMultipartFile();

        User user = UserFixture.getUserFixture("providerID", "imageUrl");
        Room room = RoomFixture.getRoom();
        Participant participant = RoomFixture.getParticipant();

        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(room, "id", 1L);
        CreateRoomRequest request = RoomFixture.getCreateRoomRequest(imageFixture);

        given(roomRepository.save(any(Room.class))).willReturn(room);
        given(participantRepository.save(any(Participant.class))).willReturn(participant);
        given(imageRepository.save(imageFixture)).willReturn("image.png");

        CreateRoomResponse response = roomService.createRoom(user, request);

        assertThat(response.roomId()).isEqualTo(room.getId());
    }
}