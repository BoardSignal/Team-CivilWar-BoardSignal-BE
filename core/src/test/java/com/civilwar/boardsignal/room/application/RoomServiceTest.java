package com.civilwar.boardsignal.room.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.civilwar.boardsignal.chat.domain.repository.ChatMessageRepository;
import com.civilwar.boardsignal.common.MultipartFileFixture;
import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.room.MeetingInfoFixture;
import com.civilwar.boardsignal.room.RoomFixture;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.MeetingInfoRepository;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.request.CreateRoomRequest;
import com.civilwar.boardsignal.room.dto.request.FixRoomRequest;
import com.civilwar.boardsignal.room.dto.request.KickOutUserRequest;
import com.civilwar.boardsignal.room.dto.response.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.response.ExitRoomResponse;
import com.civilwar.boardsignal.room.dto.response.FixRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetAllRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetEndGameUsersResponse;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import com.civilwar.boardsignal.room.dto.response.ParticipantResponse;
import com.civilwar.boardsignal.room.dto.response.ParticipantRoomResponse;
import com.civilwar.boardsignal.room.dto.response.RoomInfoResponse;
import com.civilwar.boardsignal.room.dto.response.RoomPageResponse;
import com.civilwar.boardsignal.room.exception.RoomErrorCode;
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
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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

    @Mock
    private MeetingInfoRepository meetingInfoRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

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
            1L, "김강훈", AgeGroup.TWENTY, "https", true, 99
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
        assertThat(roomInfo.time()).isEqualTo(
            findRoom.getDaySlot().getDescription() + " " + findRoom.getTimeSlot().getDescription());
        assertThat(roomInfo.startTime()).isEqualTo(findRoom.getStartTime());
        assertThat(roomInfo.subwayLine()).isEqualTo(findRoom.getSubwayLine());
        assertThat(roomInfo.subwayStation()).isEqualTo(findRoom.getSubwayStation());
        assertThat(roomInfo.place()).isEqualTo(findRoom.getPlaceName());
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
            1L, "김강훈", AgeGroup.TWENTY, "https", true, 99
        ));
        String time = meetingInfo.getMeetingTime()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

        given(roomRepository.findById(1L)).willReturn(Optional.of(findRoom));
        given(participantRepository.findParticipantByRoomId(findRoom.getId()))
            .willReturn(participantJpaDtos);

        //when
        RoomInfoResponse roomInfo = roomService.findRoomInfo(user, findRoom.getId());

        //then
        assertThat(roomInfo.roomId()).isEqualTo(findRoom.getId());
        assertThat(roomInfo.title()).isEqualTo(findRoom.getTitle());
        assertThat(roomInfo.description()).isEqualTo(findRoom.getDescription());
        assertThat(roomInfo.time()).isEqualTo(
            findRoom.getDaySlot().getDescription() + " " + findRoom.getTimeSlot().getDescription());
        assertThat(roomInfo.startTime()).isEqualTo(time);
        assertThat(roomInfo.subwayLine()).isEqualTo(meetingInfo.getLine());
        assertThat(roomInfo.subwayStation()).isEqualTo(meetingInfo.getStation());
        assertThat(roomInfo.place()).isEqualTo(meetingInfo.getMeetingPlace());
        assertThat(roomInfo.isLeader()).isFalse();
        assertThat(roomInfo.participantResponse().get(0).nickname()).isEqualTo("김강훈");
    }

    @Test
    @DisplayName("[방장은 모임을 확정시킬 수 있다.]")
    void fixRoom() throws IOException {
        //given
        User user = UserFixture.getUserFixture("prpr", "https");
        ReflectionTestUtils.setField(user, "id", 1L);

        Participant participant = RoomFixture.getParticipant();

        Room room = RoomFixture.getRoom(Gender.UNION);
        ReflectionTestUtils.setField(room, "id", 1L);

        MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 7, 31, 5, 30)
        );
        ReflectionTestUtils.setField(meetingInfo, "id", 1L);

        given(participantRepository.findByUserIdAndRoomId(user.getId(), room.getId()))
            .willReturn(Optional.of(participant));
        given(roomRepository.findById(room.getId()))
            .willReturn(Optional.of(room));
        given(meetingInfoRepository.save(any(MeetingInfo.class)))
            .willReturn(meetingInfo);

        FixRoomRequest request = RoomFixture.getFixRoomRequest();

        //when
        FixRoomResponse response = roomService.fixRoom(user, room.getId(), request);

        //then
        assertAll(
            () -> assertThat(response.roomId()).isEqualTo(room.getId()),
            () -> assertThat(response.meetingInfoId()).isEqualTo(meetingInfo.getId())
        );
    }

    @Test
    @DisplayName("[해당 방의 참가자가 아닌 회원이 모임 확정 요청을 할 시 예외가 발생한다]")
    void fixRoomInvaliParticipant() {
        //given
        User user = UserFixture.getUserFixture("erer", "qweqwe");
        ReflectionTestUtils.setField(user, "id", 1L);

        FixRoomRequest request = RoomFixture.getFixRoomRequest();

        given(participantRepository.findByUserIdAndRoomId(any(Long.class), any(Long.class)))
            .willReturn(Optional.empty());

        //when
        ThrowingCallable when = () -> roomService.fixRoom(user, 1L, request);

        //then
        assertThatThrownBy(when)
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(RoomErrorCode.INVALID_PARTICIPANT.getMessage());
    }

    @Test
    @DisplayName("[방장이 아닌 회원이 모임 확정을 할 시 예외가 발생한다]")
    void fixRoomNotLeader() {
        //given
        Participant participantNotLeader = RoomFixture.getParticipantNotLeader();

        User user = UserFixture.getUserFixture("erer", "qweqwe");
        ReflectionTestUtils.setField(user, "id", 1L);

        FixRoomRequest request = RoomFixture.getFixRoomRequest();

        given(participantRepository.findByUserIdAndRoomId(any(Long.class), any(Long.class)))
            .willReturn(Optional.of(participantNotLeader));

        //when
        ThrowingCallable when = () -> roomService.fixRoom(user, 1L, request);

        //then
        assertThatThrownBy(when)
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining(RoomErrorCode.IS_NOT_LEADER.getMessage());
    }

    @Test
    @DisplayName("[종료된 게임에 참여한 다른 참여자들을 조회할 수 있다.]")
    void getParticipantsEndGame() throws IOException {
        User user = UserFixture.getUserFixture("prpr", "https");

        Room room = RoomFixture.getRoomWithMeetingInfo(
            LocalDateTime.of(2024, 4, 5, 18, 30),
            Gender.UNION
        );
        ReflectionTestUtils.setField(room, "id", 1L);

        ParticipantJpaDto participant1 = RoomFixture.getParticipantJpaDto(1L, "게임대왕");
        ParticipantJpaDto participant2 = RoomFixture.getParticipantJpaDto(2L, "나는고수");

        given(roomRepository.findById(room.getId()))
            .willReturn(Optional.of(room));

        given(participantRepository.findParticipantByRoomId(room.getId()))
            .willReturn(List.of(participant1, participant2));

        GetEndGameUsersResponse response = roomService.getEndGameUsersResponse(
            user,
            room.getId()
        );

        MeetingInfo meeting = room.getMeetingInfo();
        List<ParticipantResponse> participants = response.participantsInfos();

        assertAll(
            () -> assertThat(response.roomId()).isEqualTo(room.getId()),
            () -> assertThat(response.title()).isEqualTo(room.getTitle()),
            () -> assertThat(response.meetingTime()).isEqualTo(meeting.getMeetingTime().toString()),
            () -> assertThat(response.peopleCount()).isEqualTo(meeting.getPeopleCount()),
            () -> assertThat(response.line()).isEqualTo(meeting.getLine()),
            () -> assertThat(response.station()).isEqualTo(meeting.getStation()),
            () -> assertThat(response.meetingPlace()).isEqualTo(meeting.getMeetingPlace()),
            () -> assertThat(response.allowedGender()).isEqualTo(
                room.getAllowedGender().getDescription()),
            () -> assertThat(participants).hasSize(2),
            () -> assertThat(participants.get(0).userId()).isEqualTo(participant1.userId()),
            () -> assertThat(participants.get(1).userId()).isEqualTo(participant2.userId())
        );
    }

    @Test
    @DisplayName("[사용자 2명이 방에 참여하면 참여자 수가 2명 늘어난다]")
    void participantTest() throws IOException {
        //given
        Long participantUserId = 50L;
        User user = UserFixture.getUserFixture("providerId", "testURL");
        ReflectionTestUtils.setField(user, "id", participantUserId);
        Long participantUserId2 = 51L;
        User user2 = UserFixture.getUserFixture("providerId", "testURL");
        ReflectionTestUtils.setField(user2, "id", participantUserId2);
        Long roomId = 1L;
        Room room = RoomFixture.getRoom(Gender.UNION);
        ReflectionTestUtils.setField(room, "id", roomId);
        given(roomRepository.findByIdWithLock(roomId)).willReturn(Optional.of(room));
        given(participantRepository.existsByUserIdAndRoomId(participantUserId, roomId)).willReturn(
            false);
        given(participantRepository.existsByUserIdAndRoomId(participantUserId2, roomId)).willReturn(
            false);

        //when
        ParticipantRoomResponse participantRoomResponse = roomService.participateRoom(
            user, roomId);
        ParticipantRoomResponse participantRoomResponse2 = roomService.participateRoom(
            user2, roomId);

        //then
        verify(participantRepository, times(2)).save(any(Participant.class));
        assertThat(participantRoomResponse2.headCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("[모임에 중복으로 참여할 수 없다]")
    void participantTest2() throws IOException {
        //given
        Long participantUserId = 50L;
        User user = UserFixture.getUserFixture("providerId", "testURL");
        ReflectionTestUtils.setField(user, "id", participantUserId);
        Long roomId = 1L;
        Room room = RoomFixture.getRoom(Gender.UNION);
        ReflectionTestUtils.setField(room, "id", roomId);
        given(participantRepository.existsByUserIdAndRoomId(participantUserId, roomId)).willReturn(
            true);

        //then
        assertThatThrownBy(
            () -> roomService.participateRoom(user, roomId)).isInstanceOf(
                ValidationException.class)
            .hasMessage(RoomErrorCode.ALREADY_PARTICIPANT.getMessage());
    }

    @Test
    @DisplayName("[사용자 3명 중 1명이 모임에서 나가면 현재 참여자는 2명이 된다]")
    void exitRoomTest() throws IOException {
        //given
        Long participantUserId = 50L;
        User user = UserFixture.getUserFixture("providerId", "testURL");
        ReflectionTestUtils.setField(user, "id", participantUserId);

        Long roomId = 1L;
        Room room = RoomFixture.getRoom(Gender.UNION);
        ReflectionTestUtils.setField(room, "id", roomId);
        room.increaseHeadCount();
        room.increaseHeadCount();

        given(roomRepository.findByIdWithLock(roomId)).willReturn(Optional.of(room));
        given(participantRepository.existsByUserIdAndRoomId(participantUserId, roomId)).willReturn(
            true);

        //when
        ExitRoomResponse exitRoomResponse = roomService.exitRoom(user, roomId);

        //then
        verify(participantRepository, times(1))
            .deleteByUserIdAndRoomId(participantUserId, roomId);
        assertThat(exitRoomResponse.headCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("[참여하지 않은 사용자는 해당 요청을 보낼 수 없다]")
    void exitRoomTest2() throws IOException {
        //given
        Long participantUserId = 50L;
        User user = UserFixture.getUserFixture("providerId", "testURL");
        ReflectionTestUtils.setField(user, "id", participantUserId);

        Long roomId = 1L;
        Room room = RoomFixture.getRoom(Gender.UNION);
        ReflectionTestUtils.setField(room, "id", roomId);
        given(participantRepository.existsByUserIdAndRoomId(participantUserId, roomId)).willReturn(
            false);

        //then
        assertThatThrownBy(
            () -> roomService.exitRoom(user, roomId)).isInstanceOf(
                NotFoundException.class)
            .hasMessage(RoomErrorCode.INVALID_PARTICIPANT.getMessage());
    }

    @Test
    @DisplayName("[방장이 모임을 삭제하면 관련 데이터들을 함께 삭제한다]")
    void deleteRoomTest() {
        //given
        Long roomId = 1L;
        Long userId = 2L;
        User user = UserFixture.getUserFixture("providerId", "testURL");
        ReflectionTestUtils.setField(user, "id", userId);
        Participant participant = Participant.of(userId, roomId, true);
        ReflectionTestUtils.setField(participant, "id", 1L);

        given(participantRepository.findByUserIdAndRoomId(userId, roomId))
            .willReturn(Optional.of(participant));

        //when
        roomService.deleteRoom(user, roomId);

        //then
        verify(participantRepository, times(1)).deleteParticipantsByRoomId(roomId);
        verify(roomRepository, times(1)).deleteById(roomId);
        verify(chatMessageRepository, times(1)).deleteByRoomId(roomId);
    }

    @Test
    @DisplayName("[방장이 아닌 사람이 해당 기능을 수행하면 예외가 발생한다]")
    void deleteRoomTest2() {
        //given
        Long roomId = 1L;
        Long userId = 2L;
        User user = UserFixture.getUserFixture("providerId", "testURL");
        ReflectionTestUtils.setField(user, "id", userId);
        Participant participant = Participant.of(userId, roomId, false);
        ReflectionTestUtils.setField(participant, "id", 1L);

        given(participantRepository.findByUserIdAndRoomId(userId, roomId))
            .willReturn(Optional.of(participant));

        //then
        assertThatThrownBy(() -> roomService.deleteRoom(user, roomId)).isInstanceOf(
                ValidationException.class)
            .hasMessage(RoomErrorCode.IS_NOT_LEADER.getMessage());
    }

    @Test
    @DisplayName("[방장은 참가자를 추방할 수 있다.]")
    void kickOutTest1() {
        //given
        Long roomId = 1L;

        Long leaderId = 50L;
        User leader = UserFixture.getUserFixture("providerId", "testURL");
        ReflectionTestUtils.setField(leader, "id", leaderId);
        Long leaderInfoId = 1L;
        Participant leaderInfo = Participant.of(leaderId, roomId, true);
        ReflectionTestUtils.setField(leaderInfo, "id", leaderInfoId);

        Long userId = 51L;
        User user = UserFixture.getUserFixture2("providerId", "testURL");
        ReflectionTestUtils.setField(user, "id", userId);
        Long userInfoId = 2L;
        Participant userInfo = Participant.of(userId, roomId, false);
        ReflectionTestUtils.setField(userInfo, "id", userInfoId);

        given(participantRepository.findByUserIdAndRoomId(leaderId, roomId)).willReturn(
            Optional.of(leaderInfo));
        given(participantRepository.findByUserIdAndRoomId(userId, roomId)).willReturn(
            Optional.of(userInfo));

        KickOutUserRequest kickOutUserRequest = new KickOutUserRequest(roomId, userId);

        //when
        roomService.kickOutUser(leader, kickOutUserRequest);

        //then
        verify(participantRepository, times(1)).deleteById(userInfoId);
    }

    @Test
    @DisplayName("[방장이 아닌 사람은 추방할 수 없다]")
    void kickOutTest2() {
        //given
        Long roomId = 1L;

        Long leaderId = 50L;
        User leader = UserFixture.getUserFixture("providerId", "testURL");
        ReflectionTestUtils.setField(leader, "id", leaderId);
        Long leaderInfoId = 1L;
        Participant leaderInfo = Participant.of(leaderId, roomId, true);
        ReflectionTestUtils.setField(leaderInfo, "id", leaderInfoId);

        Long userId = 51L;
        User user = UserFixture.getUserFixture2("providerId", "testURL");
        ReflectionTestUtils.setField(user, "id", userId);
        Long userInfoId = 2L;
        Participant userInfo = Participant.of(userId, roomId, false);
        ReflectionTestUtils.setField(userInfo, "id", userInfoId);

        given(participantRepository.findByUserIdAndRoomId(userId, roomId)).willReturn(
            Optional.of(userInfo));

        KickOutUserRequest kickOutUserRequest = new KickOutUserRequest(roomId, leaderId);

        //then
        assertThatThrownBy(() -> roomService.kickOutUser(user, kickOutUserRequest)).isInstanceOf(
            ValidationException.class).hasMessage(RoomErrorCode.IS_NOT_LEADER.getMessage());
    }

    @Test
    @DisplayName("[참가자가 아닌 사람을 추방할 수 없다]")
    void kickOutTest3() {
        //given
        Long roomId = 1L;

        Long leaderId = 50L;
        User leader = UserFixture.getUserFixture("providerId", "testURL");
        ReflectionTestUtils.setField(leader, "id", leaderId);
        Long leaderInfoId = 1L;
        Participant leaderInfo = Participant.of(leaderId, roomId, true);
        ReflectionTestUtils.setField(leaderInfo, "id", leaderInfoId);

        Long anotherUserId = 51L;

        given(participantRepository.findByUserIdAndRoomId(leaderId, roomId)).willReturn(
            Optional.of(leaderInfo));

        KickOutUserRequest kickOutUserRequest = new KickOutUserRequest(roomId, anotherUserId);

        //then
        assertThatThrownBy(() -> roomService.kickOutUser(leader, kickOutUserRequest)).isInstanceOf(
            ValidationException.class).hasMessage(RoomErrorCode.INVALID_PARTICIPANT.getMessage());
    }
}