package com.civilwar.boardsignal.room.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.room.MeetingInfoFixture;
import com.civilwar.boardsignal.room.RoomFixture;
import com.civilwar.boardsignal.room.domain.constants.DaySlot;
import com.civilwar.boardsignal.room.domain.constants.RoomStatus;
import com.civilwar.boardsignal.room.domain.constants.TimeSlot;
import com.civilwar.boardsignal.room.domain.constants.WeekDay;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.request.ApiCreateRoomRequest;
import com.civilwar.boardsignal.room.dto.request.ApiFixRoomRequest;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import com.civilwar.boardsignal.room.dto.request.KickOutUserRequest;
import com.civilwar.boardsignal.room.infrastructure.repository.MeetingInfoJpaRepository;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.constants.Role;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@DisplayName("[RoomController 테스트]")
class RoomControllerTest extends ApiTestSupport {

    private final String title = "달무티 할 사람";
    private final String description = "20대만";
    private final String station = "사당역";
    private final DaySlot daySlot = DaySlot.WEEKDAY;
    private final TimeSlot timeSlot = TimeSlot.AM;
    private final List<Category> categories = List.of(Category.FAMILY, Category.PARTY);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private MeetingInfoJpaRepository meetingInfoRepository;
    @Autowired
    private TokenProvider tokenProvider;
    @MockBean
    private Supplier<LocalDateTime> nowTime;

    @Test
    @DisplayName("[사용자는 방을 생성할 수 있다.]")
    void createRoom() throws Exception {
        ApiCreateRoomRequest request = new ApiCreateRoomRequest(
            "무슨무슨 모임입니다!",
            "재밌게 하려고 모집중",
            1,
            6,
            "주말",
            "오전",
            "토요일 오후 3:30",
            21,
            25,
            "7호선",
            "이수역",
            "레드버튼 사당점",
            List.of("가족게임", "컬렉터블게임"),
            true
        );

        String fileName = "testFile.png";
        MockMultipartFile data = new MockMultipartFile(
            "data",
            null,
            "application/json",
            toJson(request).getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile image = new MockMultipartFile(
            "image",
            fileName,
            "image/png",
            new FileInputStream("src/test/resources/" + fileName)
        );

        ResultActions resultActions = mockMvc.perform(
                multipart(HttpMethod.POST, "/api/v1/rooms")
                    .file(image)
                    .file(data)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk());

        Room room = roomRepository.findById(1L).orElseThrow();

        resultActions.andExpect(jsonPath("$.roomId").value(room.getId()));
        assertThat(room.getAllowedGender()).isEqualTo(Gender.UNION);
    }

    @Test
    @DisplayName("[방 생성 시 이성 허용이 불가능하면, 방장의 성별이 방의 저장된다.]")
    void createRoom2() throws Exception {
        ApiCreateRoomRequest request = new ApiCreateRoomRequest(
            "무슨무슨 모임입니다!",
            "재밌게 하려고 모집중",
            1,
            6,
            "주말",
            "오전",
            "토요일 오후 3:30",
            21,
            25,
            "7호선",
            "이수역",
            "레드버튼 사당점",
            List.of("가족게임", "컬렉터블게임"),
            false
        );

        String fileName = "testFile.png";
        MockMultipartFile data = new MockMultipartFile(
            "data",
            null,
            "application/json",
            toJson(request).getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile image = new MockMultipartFile(
            "image",
            fileName,
            "image/png",
            new FileInputStream("src/test/resources/" + fileName)
        );

        ResultActions resultActions = mockMvc.perform(
                multipart(HttpMethod.POST, "/api/v1/rooms")
                    .file(image)
                    .file(data)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk());

        Room room = roomRepository.findById(1L).orElseThrow();

        resultActions.andExpect(jsonPath("$.roomId").value(room.getId()));
        assertThat(room.getAllowedGender()).isEqualTo(loginUser.getGender());
    }

    @Test
    @DisplayName("[사용자는 자신이 참여했던 모임을 조회할 수 있다.]")
    void getMyEndGameTest() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.of(2024, 2, 21, 20, 0, 0);
        LocalDateTime before = LocalDateTime.of(2024, 2, 20, 20, 0, 0);
        given(nowTime.get()).willReturn(now);

        //방 생성
        for (int i = 0; i < 6; i++) {
            //방 생성
            Room room = RoomFixture.getRoom(Gender.UNION);
            roomRepository.save(room);

            //방 참가
            Participant participant = Participant.of(loginUser.getId(), room.getId(), true);
            participantRepository.save(participant);

            //모임 확정
            MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(before);
            meetingInfoRepository.save(meetingInfo);
            room.fixRoom(meetingInfo);
            roomRepository.save(room);
        }

        //then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("size", "5");
        mockMvc.perform(get("/api/v1/rooms/my/end-games")
                .header(AUTHORIZATION, accessToken)
                .params(params))
            .andExpect(jsonPath("$.size").value(5))
            .andExpect(jsonPath("$.hasNext").value(true))
            .andExpect(jsonPath("$.roomsInfos.length()").value(5));
    }

    @Test
    @DisplayName("[사용자의 non-fix 모임, 확정 시간이 지나지 않은 모임은 불러오지 않는다]")
    void getMyEndGameTest2() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.of(2024, 2, 21, 20, 0, 0);
        LocalDateTime after = LocalDateTime.of(2024, 2, 22, 20, 0, 0);
        given(nowTime.get()).willReturn(now);

        //모임 확정 시간이 아직 지나지 않은 방 생성
        for (int i = 0; i < 5; i++) {
            //방 생성
            Room room = RoomFixture.getRoom(Gender.UNION);
            roomRepository.save(room);

            //방 참가
            Participant participant = Participant.of(loginUser.getId(), room.getId(), true);
            participantRepository.save(participant);

            //모임 확정
            MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(after);
            meetingInfoRepository.save(meetingInfo);
            room.fixRoom(meetingInfo);
            roomRepository.save(room);
        }

        //non-fix 방 생성
        for (int i = 0; i < 5; i++) {
            //방 생성
            Room room = RoomFixture.getRoom(Gender.UNION);
            roomRepository.save(room);

            //방 참가
            Participant participant = Participant.of(loginUser.getId(), room.getId(), true);
            participantRepository.save(participant);
        }

        //then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("size", "100");
        mockMvc.perform(get("/api/v1/rooms/my/end-games")
                .header(AUTHORIZATION, accessToken)
                .params(params))
            .andExpect(jsonPath("$.size").value(100))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.roomsInfos.length()").value(0));

    }

    @Test
    @DisplayName("[사용자는 모든 조건에 맞는 방을 필터링 하여 조회할 수 있다]")
    void getSearchRoom() throws Exception {
        //given
        for (int i = 0; i < 100; i++) {
            Room room = RoomFixture.getRoom(Gender.UNION);
            roomRepository.save(room);
        }
        Room anotherRoom = RoomFixture.getAnotherRoom(
            title,
            description,
            station,
            daySlot,
            timeSlot,
            categories,
            Gender.MALE
        );
        roomRepository.save(anotherRoom);

        //then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("size", "100");
        params.add("station", station);
        params.add("time", "평일_오전");
        params.add("category", "파티게임");
        params.add("gender", "남성");

        mockMvc.perform(get("/api/v1/rooms/filter")
                .header(AUTHORIZATION, accessToken)
                .params(params))
            .andExpect(jsonPath("$.size").value(100))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.roomsInfos.length()").value(1));
    }

    @Test
    @DisplayName("[조건이 하나라도 다르면 필터링 대상에서 제외된다]")
    void getSearchRoom2() throws Exception {
        //given
        for (int i = 0; i < 100; i++) {
            Room room = RoomFixture.getRoom(Gender.UNION);
            roomRepository.save(room);
        }
        Room anotherRoom = RoomFixture.getAnotherRoom(
            title,
            description,
            station,
            daySlot,
            timeSlot,
            categories,
            Gender.UNION
        );
        roomRepository.save(anotherRoom);

        //then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("size", "100");
        params.add("station", station);
        params.add("time", "평일_오전");
        params.add("category", "파티게임");
        params.add("gender", "남성");

        mockMvc.perform(get("/api/v1/rooms/filter")
                .header(AUTHORIZATION, accessToken)
                .params(params))
            .andExpect(jsonPath("$.size").value(100))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.roomsInfos.length()").value(0));
    }

    @Test
    @DisplayName("[방장이 non-fix 방 상세정보를 조회한다]")
    void getRoomInfoTest() throws Exception {
        //given
        Room room = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room);

        Participant participant = Participant.of(loginUser.getId(), room.getId(), true);
        participantRepository.save(participant);

        mockMvc.perform(get("/api/v1/rooms/" + room.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(jsonPath("$.roomId").value(room.getId()))
            .andExpect(jsonPath("$.title").value(room.getTitle()))
            .andExpect(jsonPath("$.startTime").value(
                room.getDaySlot().getDescription()
                    + " " + room.getTimeSlot().getDescription()))
            .andExpect(jsonPath("$.minParticipants").value(3))
            .andExpect(jsonPath("$.maxParticipants").value(6))
            .andExpect(jsonPath("$.isFix").value("미확정"))
            .andExpect(jsonPath("$.isLeader").value(true))
            .andExpect(jsonPath("$.place").value(
                room.getSubwayStation() + " " + room.getPlaceName()
            ))
            .andExpect(jsonPath("$.allowedGender").value(Gender.UNION.getDescription()))
            .andExpect(jsonPath("$.participantResponse[0].isLeader")
                .value(true))
            .andExpect(jsonPath("$.participantResponse[0].nickname")
                .value("injuning"));
    }

    @Test
    @DisplayName("[방장이 아닌 사람이 fix 방 상세정보를 조회한다]")
    void getRoomInfoTest2() throws Exception {
        //given
        User anotherUser = UserFixture.getUserFixture2("providerId", "imageUrl");
        userRepository.save(anotherUser);

        Room room = RoomFixture.getRoom(Gender.MALE);
        roomRepository.save(room);

        Participant participant = Participant.of(anotherUser.getId(), room.getId(), true);
        participantRepository.save(participant);

        MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 2, 26, 20, 3, 59));
        meetingInfoRepository.save(meetingInfo);
        room.fixRoom(meetingInfo);
        roomRepository.save(room);

        mockMvc.perform(get("/api/v1/rooms/" + room.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(jsonPath("$.roomId").value(room.getId()))
            .andExpect(jsonPath("$.title").value(room.getTitle()))
            .andExpect(jsonPath("$.startTime").value(
                meetingInfo.getMeetingTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))
            .andExpect(jsonPath("$.place").value(
                meetingInfo.getStation()
                    + " " + meetingInfo.getMeetingPlace()
            ))
            .andExpect(jsonPath("$.isFix").value("확정"))
            .andExpect(jsonPath("$.isLeader").value(false))
            .andExpect(jsonPath("$.allowedGender").value(Gender.MALE.getDescription()))
            .andExpect(jsonPath("$.participantResponse.size()").value(1))
            .andExpect(jsonPath("$.participantResponse[0].nickname")
                .value("macbook"));
    }

    @Test
    @DisplayName("[비로그인 유저가 fix 방 상세정보를 조회한다]")
    void getRoomInfoTest3() throws Exception {
        //given
        User anotherUser = UserFixture.getUserFixture2("providerId", "imageUrl");
        userRepository.save(anotherUser);

        Room room = RoomFixture.getRoom(Gender.MALE);
        roomRepository.save(room);

        Participant participant = Participant.of(anotherUser.getId(), room.getId(), true);
        participantRepository.save(participant);

        MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 2, 26, 20, 3, 59));
        meetingInfoRepository.save(meetingInfo);
        room.fixRoom(meetingInfo);
        roomRepository.save(room);

        mockMvc.perform(get("/api/v1/rooms/" + room.getId()))
            .andExpect(jsonPath("$.roomId").value(room.getId()))
            .andExpect(jsonPath("$.title").value(room.getTitle()))
            .andExpect(jsonPath("$.startTime").value(
                meetingInfo.getMeetingTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))))
            .andExpect(jsonPath("$.place").value(
                meetingInfo.getStation()
                    + " " + meetingInfo.getMeetingPlace()
            ))
            .andExpect(jsonPath("$.isFix").value("확정"))
            .andExpect(jsonPath("$.isLeader").value(false))
            .andExpect(jsonPath("$.allowedGender").value(Gender.MALE.getDescription()))
            .andExpect(jsonPath("$.participantResponse.size()").value(1))
            .andExpect(jsonPath("$.participantResponse[0].nickname")
                .value("macbook"));
    }

    @Test
    @DisplayName("[방장은 모임을 확정시킬 수 있다.]")
    void fixRoom() throws Exception {
        //given
        Room room = RoomFixture.getRoom(Gender.MALE);
        roomRepository.save(room);
        ApiFixRoomRequest request = new ApiFixRoomRequest(
            LocalDateTime.of(2024, 3, 31, 5, 30),
            "수요일",
            5,
            "2호선",
            "강남역",
            "레드버튼"
        );
        Participant participant = Participant.of(loginUser.getId(), room.getId(), true);
        participantRepository.save(participant);

        mockMvc.perform(post("/api/v1/rooms/fix/{roomId}", room.getId())
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.roomId").value(room.getId())
            );

        Room findRoom = roomRepository.findById(room.getId()).orElseThrow();
        MeetingInfo meetingInfo = findRoom.getMeetingInfo();

        assertAll(
            () -> assertThat(findRoom.getStatus()).isEqualTo(RoomStatus.FIX),
            () -> assertThat(meetingInfo.getMeetingTime()).isEqualTo(request.meetingTime()),
            () -> assertThat(meetingInfo.getWeekDay()).isEqualTo(WeekDay.of(request.weekDay())),
            () -> assertThat(meetingInfo.getPeopleCount()).isEqualTo(request.peopleCount()),
            () -> assertThat(meetingInfo.getLine()).isEqualTo(request.line()),
            () -> assertThat(meetingInfo.getStation()).isEqualTo(request.station()),
            () -> assertThat(meetingInfo.getMeetingPlace()).isEqualTo(request.meetingPlace())
        );
    }
  
    @Test
    @DisplayName("[종료된 게임에 같이 참여한 참여자들을 조회할 수 있다.]")
    void getParticipantsEndGame() throws Exception {
        //given
        User user = UserFixture.getUserFixture2("provider", "https");
        userRepository.save(user);

        MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 2, 2, 5, 30)
        );
        MeetingInfo savedMeeting = meetingInfoRepository.save(meetingInfo);

        Room room = RoomFixture.getRoom(Gender.MALE);
        room.fixRoom(savedMeeting);
        Room savedRoom = roomRepository.save(room);

        Participant participant1 = Participant.of(loginUser.getId(), savedRoom.getId(), false);
        Participant participant2 = Participant.of(user.getId(), savedRoom.getId(), false);
        participantRepository.save(participant1);
        participantRepository.save(participant2);

        //then
        mockMvc.perform(get("/api/v1/rooms/end-game/{roomId}", savedRoom.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.roomId").value(savedRoom.getId()),
                jsonPath("title").value(savedRoom.getTitle()),
                jsonPath("$.meetingTime").value(meetingInfo.getMeetingTime().toString()),
                jsonPath("$.weekDay").value(meetingInfo.getWeekDay().getDescription()),
                jsonPath("$.peopleCount").value(meetingInfo.getPeopleCount()),
                jsonPath("$.participantsInfos[0].userId").value(user.getId()),
                jsonPath("$.participantsInfos[0].nickname").value(user.getNickname()),
                jsonPath("$.participantsInfos[0].ageGroup").value(
                    user.getAgeGroup().getDescription()),
                jsonPath("$.participantsInfos[0].profileImageUrl").value(user.getProfileImageUrl())
            );
    }

    @Test
    @DisplayName("[방의 참가자는 모임 확정을 취소시킬 수 있다.]")
    void unFixRoom() throws Exception {
        //given
        MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 2, 2, 5, 30)
        );
        MeetingInfo savedMeeting = meetingInfoRepository.save(meetingInfo);

        Room room = RoomFixture.getRoom(Gender.MALE);
        room.fixRoom(savedMeeting);
        Room savedRoom = roomRepository.save(room);

        Participant participant = Participant.of(loginUser.getId(), savedRoom.getId(), false);
        participantRepository.save(participant);

        //when
        mockMvc.perform(delete("/api/v1/rooms/unfix/{roomId}", savedRoom.getId())
            .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk());

        //then
        Room findRoom = roomRepository.findById(savedRoom.getId()).orElseThrow();
        assertThat(findRoom.getMeetingInfo()).isNull();
    }

    @Test
    @DisplayName("[사용자는 방에 참여할 수 있다]")
    void participantRoomTest() throws Exception {
        //given
        Room room = RoomFixture.getRoom(Gender.MALE);
        roomRepository.save(room);

        //then
        mockMvc.perform(post("/api/v1/rooms/in/" + room.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(jsonPath("$.headCount").value(2));
    }

    @Test
    @DisplayName("[이미 참여되어 있는 사용자는 참여할 수 없다]")
    void participantRoomTest2() throws Exception {
        //given
        //총 2명 참여 중
        Room room = RoomFixture.getRoom(Gender.MALE);
        roomRepository.save(room);
        Participant participant = Participant.of(loginUser.getId(), room.getId(), false);
        participantRepository.save(participant);
        room.increaseHeadCount();

        //then
        mockMvc.perform(post("/api/v1/rooms/in/" + room.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(
                (result) -> assertThat(result.getResolvedException()).getClass().isAssignableFrom(
                    ValidationException.class))
            .andExpect(status().is4xxClientError());
        assertThat(room.getHeadCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("[사용자는 참여한 방을 나갈 수 있다]")
    void exitRoomTest() throws Exception {
        //given
        Room room = RoomFixture.getRoom(Gender.MALE);
        roomRepository.save(room);
        Participant participant = Participant.of(loginUser.getId(), room.getId(), false);
        participantRepository.save(participant);
        room.increaseHeadCount();
        roomRepository.save(room);

        //then
        mockMvc.perform(post("/api/v1/rooms/out/" + room.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(jsonPath("$.headCount").value(1));
    }

    @Test
    @DisplayName("[해당 방에 참여하지 않은 사용자는 방 나가기 요청을 보낼 수 없다]")
    void exitRoomTest2() throws Exception {
        //given
        Room room = RoomFixture.getRoom(Gender.MALE);
        roomRepository.save(room);

        //then
        mockMvc.perform(post("/api/v1/rooms/out/" + room.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(
                (result) -> assertThat(result.getResolvedException()).getClass().isAssignableFrom(
                    NotFoundException.class))
            .andExpect(status().is4xxClientError());
        assertThat(room.getHeadCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("[방장은 모임을 삭제하며, 관련 데이터들도 삭제한다]")
    void deleteRoomTest() throws Exception {
        //given
        User user = UserFixture.getUserFixture("providerId", "testURL");
        userRepository.save(user);
        Room room = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room);

        Participant leader = Participant.of(loginUser.getId(), room.getId(), true);
        participantRepository.save(leader);
        room.increaseHeadCount();
        Participant notLeader = Participant.of(user.getId(), room.getId(), false);
        participantRepository.save(notLeader);
        room.increaseHeadCount();

        //when
        mockMvc.perform(delete("/api/v1/rooms/" + room.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk());

        //then
        List<ParticipantJpaDto> participants = participantRepository.findParticipantByRoomId(
            room.getId());
        Optional<Room> optionalRoom = roomRepository.findById(room.getId());

        assertThat(participants).isEmpty();
        assertThat(optionalRoom).isEmpty();
    }

    @Test
    @DisplayName("[방장이 아닌 사용자가 모임 삭제를 할 수 없다]")
    void deleteRoomTest2() throws Exception {
        //given
        User user = UserFixture.getUserFixture("providerId", "testURL");
        userRepository.save(user);
        Room room = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room);

        Participant leader = Participant.of(loginUser.getId(), room.getId(), false);
        participantRepository.save(leader);
        room.increaseHeadCount();
        Participant notLeader = Participant.of(user.getId(), room.getId(), true);
        participantRepository.save(notLeader);
        room.increaseHeadCount();

        //then
        mockMvc.perform(delete("/api/v1/rooms/" + room.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(
                (result) -> assertThat(result.getResolvedException()).getClass().isAssignableFrom(
                    ValidationException.class))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("[방장은 참가자를 추방할 수 있다]")
    void kickOutTest() throws Exception {
        //given
        Room room = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room);

        User notLeader = UserFixture.getUserFixture2("providerId", "testURL");
        userRepository.save(notLeader);

        Participant leaderInfo = Participant.of(loginUser.getId(), room.getId(), true);
        participantRepository.save(leaderInfo);
        Participant userInfo = Participant.of(notLeader.getId(), room.getId(), false);
        participantRepository.save(userInfo);

        KickOutUserRequest request = new KickOutUserRequest(room.getId(), notLeader.getId());

        //then
        mockMvc.perform(post("/api/v1/rooms/kick")
            .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
            .andExpect(status().isOk());

        List<Participant> result = participantRepository.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("[방장이 아닌 사람은 추방할 수 없다]")
    void kickOutTest2() throws Exception {
        //given
        Room room = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room);

        User notLeader = UserFixture.getUserFixture2("providerId", "testURL");
        userRepository.save(notLeader);
        Token notLeaderToken = tokenProvider.createToken(notLeader.getId(), Role.USER);
        String notLeaderAccessToken = "Bearer " + notLeaderToken.accessToken();

        Participant leaderInfo = Participant.of(loginUser.getId(), room.getId(), true);
        participantRepository.save(leaderInfo);
        Participant notLeaderInfo = Participant.of(notLeader.getId(), room.getId(), false);
        participantRepository.save(notLeaderInfo);

        KickOutUserRequest request = new KickOutUserRequest(room.getId(), loginUser.getId());

        //then
        mockMvc.perform(post("/api/v1/rooms/kick")
                .header(AUTHORIZATION, notLeaderAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
            .andExpect((result) -> assertThat(result.getResolvedException()).getClass().isAssignableFrom(
                ValidationException.class))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("[참가자가 아닌 사람을 추방할 수 없다]")
    void kickOutTest3() throws Exception {
        //given
        Room room = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room);

        User notParticipant = UserFixture.getUserFixture2("providerId", "testURL");
        userRepository.save(notParticipant);

        Participant leaderInfo = Participant.of(loginUser.getId(), room.getId(), true);
        participantRepository.save(leaderInfo);

        KickOutUserRequest request = new KickOutUserRequest(room.getId(), notParticipant.getId());

        //then
        mockMvc.perform(post("/api/v1/rooms/kick")
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
            .andExpect((result) -> assertThat(result.getResolvedException()).getClass().isAssignableFrom(
                ValidationException.class))
            .andExpect(status().is4xxClientError());
    }
}