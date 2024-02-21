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
import com.civilwar.boardsignal.room.dto.response.GetAllRoomResponse;
import com.civilwar.boardsignal.room.dto.response.RoomPageResponse;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("[RoomService 테스트]")
@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private Supplier<LocalDateTime> time;

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


    @Test
    @DisplayName("[자신이 참여한 방 중 non-fix 방은 보여주지 않는다.]")
    void findMyEndGameTest() throws IOException {
        //given
        Long userId = 1L;
        int PAGE_NUMBER = 0;
        int PAGE_SIZE = 5;
        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        //non-fix인 방 -> 30개 저장
        List<Room> testResult = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Room room = RoomFixture.getRoom();
            ReflectionTestUtils.setField(room, "id", Long.parseLong(String.valueOf(i)));
            testResult.add(room);
        }
        given(roomRepository.findMyGame(userId)).willReturn(testResult);

        //when
        RoomPageResponse<GetAllRoomResponse> myEndGame = roomService.findMyEndGame(userId,
            pageRequest);

        //then
        assertThat(myEndGame.roomsInfos()).isEmpty();
    }

    @Test
    @DisplayName("[자신이 참여한 fix 방 중 모임 확정 시간이 지난 방을 보여준다.]")
    void findMyEndGameTest2() throws IOException {
        //given
        Long userId = 1L;
        int PAGE_NUMBER = 0;
        int PAGE_SIZE = 5;
        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        LocalDateTime before = LocalDateTime.of(2024, Month.FEBRUARY, 2, 20, 0, 0);
        LocalDateTime now = LocalDateTime.of(2024, Month.FEBRUARY, 21, 0, 0, 0);
        given(time.get()).willReturn(now);

        //fix이면서 시간이 지난 방 -> 30개
        List<Room> testResult = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Room room = RoomFixture.getRoomWithMeetingInfo(before);
            ReflectionTestUtils.setField(room, "id", Long.parseLong(String.valueOf(i)));
            testResult.add(room);
        }
        given(roomRepository.findMyGame(userId)).willReturn(testResult);

        //when
        RoomPageResponse<GetAllRoomResponse> myEndGame = roomService.findMyEndGame(userId,
            pageRequest);

        //then
        assertThat(myEndGame.roomsInfos()).hasSize(5);
        assertThat(myEndGame.hasNext()).isTrue();
    }

    @Test
    @DisplayName("[자신이 참여한 fix 방 중 모임 확정 시간이 지나지 않은 방은 안 보여준다.]")
    void findMyEndGameTest3() throws IOException {
        //given
        Long userId = 1L;
        int PAGE_NUMBER = 0;
        int PAGE_SIZE = 5;
        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        LocalDateTime after = LocalDateTime.of(2024, Month.FEBRUARY, 22, 20, 0, 0);
        LocalDateTime now = LocalDateTime.of(2024, Month.FEBRUARY, 21, 0, 0, 0);
        given(time.get()).willReturn(now);

        //fix이면서 아직 시간이 지나지 않은 방 -> 30개
        List<Room> testResult = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Room room = RoomFixture.getRoomWithMeetingInfo(after);
            ReflectionTestUtils.setField(room, "id", Long.parseLong(String.valueOf(i)));
            testResult.add(room);
        }
        given(roomRepository.findMyGame(userId)).willReturn(testResult);

        //when
        RoomPageResponse<GetAllRoomResponse> myEndGame = roomService.findMyEndGame(userId,
            pageRequest);

        //then
        assertThat(myEndGame.roomsInfos()).isEmpty();
        assertThat(myEndGame.hasNext()).isFalse();
    }

    @Test
    @DisplayName("[남은 요소들이 있다면 hasNext는 true를 반환한다.]")
    void findMyEndGameTest4() throws IOException {
        //given
        Long userId = 1L;
        int PAGE_NUMBER = 0;
        int PAGE_SIZE = 26;
        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        LocalDateTime before = LocalDateTime.of(2024, Month.FEBRUARY, 2, 20, 0, 0);
        LocalDateTime now = LocalDateTime.of(2024, Month.FEBRUARY, 21, 0, 0, 0);
        given(time.get()).willReturn(now);

        //fix이면서 시간이 지난 방 -> 30개
        List<Room> testResult = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Room room = RoomFixture.getRoomWithMeetingInfo(before);
            ReflectionTestUtils.setField(room, "id", Long.parseLong(String.valueOf(i)));
            testResult.add(room);
        }
        given(roomRepository.findMyGame(userId)).willReturn(testResult);

        //when
        RoomPageResponse<GetAllRoomResponse> myEndGame = roomService.findMyEndGame(userId,
            pageRequest);

        //then
        assertThat(myEndGame.roomsInfos()).hasSize(26);
        assertThat(myEndGame.hasNext()).isTrue();
    }

    @Test
    @DisplayName("[남은 요소들이 없다면 hasNext는 false를 반환한다.]")
    void findMyEndGameTest5() throws IOException {
        //given
        Long userId = 1L;
        int PAGE_NUMBER = 0;
        int PAGE_SIZE = 31;
        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        LocalDateTime before = LocalDateTime.of(2024, Month.FEBRUARY, 2, 20, 0, 0);
        LocalDateTime now = LocalDateTime.of(2024, Month.FEBRUARY, 21, 0, 0, 0);
        given(time.get()).willReturn(now);

        //fix이면서 시간이 지난 방 -> 30개
        List<Room> testResult = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Room room = RoomFixture.getRoomWithMeetingInfo(before);
            ReflectionTestUtils.setField(room, "id", Long.parseLong(String.valueOf(i)));
            testResult.add(room);
        }
        given(roomRepository.findMyGame(userId)).willReturn(testResult);

        //when
        RoomPageResponse<GetAllRoomResponse> myEndGame = roomService.findMyEndGame(userId,
            pageRequest);

        //then
        assertThat(myEndGame.roomsInfos()).hasSize(30);
        assertThat(myEndGame.hasNext()).isFalse();
    }
}