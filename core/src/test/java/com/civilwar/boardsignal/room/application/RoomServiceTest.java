package com.civilwar.boardsignal.room.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.civilwar.boardsignal.common.MultipartFileFixture;
import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.room.MeetingInfoFixture;
import com.civilwar.boardsignal.room.RoomFixture;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.request.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.response.CreateRoomRequest;
import com.civilwar.boardsignal.room.dto.response.GetAllRoomResponse;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import com.civilwar.boardsignal.room.dto.response.RoomInfoResponse;
import com.civilwar.boardsignal.room.dto.response.RoomPageResponse;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

    private String concat(String string1, String string2) {
        return string1
            + " "
            + string2;
    }

    @Test
    @DisplayName("[방을 생성할 수 있다.]")
    void createRoom() throws IOException {
        MockMultipartFile imageFixture = MultipartFileFixture.getMultipartFile();

        User user = UserFixture.getUserFixture("providerID", "imageUrl");
        Room room = RoomFixture.getRoom(user.getGender());
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
            Room room = RoomFixture.getRoomWithMeetingInfo(before, Gender.UNION);
            ReflectionTestUtils.setField(room, "id", Long.parseLong(String.valueOf(i)));
            testResult.add(room);
        }
        given(roomRepository.findMyFixRoom(userId)).willReturn(testResult);

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
            Room room = RoomFixture.getRoomWithMeetingInfo(after, Gender.UNION);
            ReflectionTestUtils.setField(room, "id", Long.parseLong(String.valueOf(i)));
            testResult.add(room);
        }
        given(roomRepository.findMyFixRoom(userId)).willReturn(testResult);

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
            Room room = RoomFixture.getRoomWithMeetingInfo(before, Gender.UNION);
            ReflectionTestUtils.setField(room, "id", Long.parseLong(String.valueOf(i)));
            testResult.add(room);
        }
        given(roomRepository.findMyFixRoom(userId)).willReturn(testResult);

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
            Room room = RoomFixture.getRoomWithMeetingInfo(before, Gender.UNION);
            ReflectionTestUtils.setField(room, "id", Long.parseLong(String.valueOf(i)));
            testResult.add(room);
        }
        given(roomRepository.findMyFixRoom(userId)).willReturn(testResult);

        //when
        RoomPageResponse<GetAllRoomResponse> myEndGame = roomService.findMyEndGame(userId,
            pageRequest);

        //then
        assertThat(myEndGame.roomsInfos()).hasSize(30);
        assertThat(myEndGame.hasNext()).isFalse();
    }

    @Test
    @DisplayName("[non-fix 방을 방장이 조회하면, 방장 여부와 & 생성 시 작성한 방 정보가 조회된다]")
    void findRoomInfoTest() throws IOException {
        //given
        User user = UserFixture.getUserFixture("providerId", "imageUrl");
        ReflectionTestUtils.setField(user, "id", 1L);
        Long loginUserId = 1L;
        Room findRoom = RoomFixture.getRoom(Gender.UNION);
        ReflectionTestUtils.setField(findRoom, "id", 1L);
        List<ParticipantJpaDto> participantJpaDtos = List.of(new ParticipantJpaDto(
            1L, "김강훈", AgeGroup.TWENTY, true, 99
        ));
        String place = concat(findRoom.getSubwayStation(), findRoom.getPlaceName());
        String time = concat(findRoom.getDaySlot().getDescription(),
            findRoom.getTimeSlot().getDescription());

        given(roomRepository.findById(1L)).willReturn(Optional.of(findRoom));
        given(participantRepository.findParticipantByRoomId(findRoom.getId()))
            .willReturn(participantJpaDtos);

        //when
        RoomInfoResponse roomInfo = roomService.findRoomInfo(user, findRoom.getId());

        //then
        assertThat(roomInfo.roomId()).isEqualTo(findRoom.getId());
        assertThat(roomInfo.title()).isEqualTo(findRoom.getTitle());
        assertThat(roomInfo.description()).isEqualTo(findRoom.getDescription());
        assertThat(roomInfo.startTime()).isEqualTo(time);
        assertThat(roomInfo.place()).isEqualTo(place);
        assertThat(roomInfo.isLeader()).isTrue();
        assertThat(roomInfo.participantResponse().get(0).nickname()).isEqualTo("김강훈");
    }

    @Test
    @DisplayName("[fix 방을 방장이 아닌 사람이 조회하면, 방장 여부와 & 모임 확정 정보가 조회된다]")
    void findRoomInfoTest2() throws IOException {
        //given
        User user = UserFixture.getUserFixture("providerId", "imageUrl");
        ReflectionTestUtils.setField(user, "id", 2L);
        Room findRoom = RoomFixture.getRoom(Gender.UNION);
        ReflectionTestUtils.setField(findRoom, "id", 1L);
        MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2023, 2, 26, 20, 3, 59));
        findRoom.fixRoom(meetingInfo);

        List<ParticipantJpaDto> participantJpaDtos = List.of(new ParticipantJpaDto(
            1L, "김강훈", AgeGroup.TWENTY, true, 99
        ));
        String place = concat(meetingInfo.getStation(), meetingInfo.getMeetingPlace());
        String time = meetingInfo.getMeetingTime()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        given(roomRepository.findById(1L)).willReturn(Optional.of(findRoom));
        given(participantRepository.findParticipantByRoomId(findRoom.getId()))
            .willReturn(participantJpaDtos);

        //when
        RoomInfoResponse roomInfo = roomService.findRoomInfo(user, findRoom.getId());

        //then
        assertThat(roomInfo.roomId()).isEqualTo(findRoom.getId());
        assertThat(roomInfo.title()).isEqualTo(findRoom.getTitle());
        assertThat(roomInfo.description()).isEqualTo(findRoom.getDescription());
        assertThat(roomInfo.startTime()).isEqualTo(time);
        assertThat(roomInfo.place()).isEqualTo(place);
        assertThat(roomInfo.isLeader()).isFalse();
        assertThat(roomInfo.participantResponse().get(0).nickname()).isEqualTo("김강훈");
    }
}