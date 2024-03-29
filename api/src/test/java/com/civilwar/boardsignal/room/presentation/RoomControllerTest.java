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
import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.domain.repository.ChatMessageRepository;
import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.review.ReviewFixture;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.repository.ReviewRepository;
import com.civilwar.boardsignal.room.MeetingInfoFixture;
import com.civilwar.boardsignal.room.RoomFixture;
import com.civilwar.boardsignal.room.domain.constants.DaySlot;
import com.civilwar.boardsignal.room.domain.constants.RoomStatus;
import com.civilwar.boardsignal.room.domain.constants.TimeSlot;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.entity.RoomBlackList;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomBlackListRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.request.ApiCreateRoomRequest;
import com.civilwar.boardsignal.room.dto.request.ApiFixRoomRequest;
import com.civilwar.boardsignal.room.dto.request.KickOutUserRequest;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import com.civilwar.boardsignal.room.infrastructure.repository.MeetingInfoJpaRepository;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.constants.Role;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Disabled;
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
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private RoomBlackListRepository blackListRepository;
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
    @DisplayName("[사용자는 자신이 참여중인 채팅방을 조회할 수 있다, 이 때 fix 되고 시간이 지난방은 조회되지 않는다]")
    void getMyGameTest() throws Exception {
        /**
         * room1 -> 참가, fix, 시간이 지난 모임,
         * room2 -> 참가, fix, 시간이 지나지 않은 모임 -> o
         * room3 -> 참가, unfix -> o
         * room4 -> 미참가
         * room5 -> 참가, fix, 모임 확정 날짜가 당일인 모임 -> o
         */
        //given
        LocalDateTime now = LocalDateTime.of(2024, 2, 21, 20, 0, 0);
        LocalDateTime before = LocalDateTime.of(2024, 2, 20, 20, 0, 0);
        LocalDateTime after = LocalDateTime.of(2024, 2, 22, 20, 0, 0);
        LocalDateTime today = LocalDateTime.of(2024, 2, 21, 18, 0, 0);
        given(nowTime.get()).willReturn(now);

        //방 5개 중
        Room room1 = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room1);
        Room room2 = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room2);
        Room room3 = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room3);
        Room room4 = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room4);
        Room room5 = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room5);

        //방 4개 참가
        Participant participant = Participant.of(loginUser.getId(), room1.getId(), true);
        participantRepository.save(participant);
        Participant participant2 = Participant.of(loginUser.getId(), room2.getId(), true);
        participantRepository.save(participant2);
        Participant participant3 = Participant.of(loginUser.getId(), room3.getId(), true);
        participantRepository.save(participant3);
        Participant participant5 = Participant.of(loginUser.getId(), room5.getId(), true);
        participantRepository.save(participant5);

        //방 3개 확정
        MeetingInfo meetingInfo1 = MeetingInfoFixture.getMeetingInfo(before);
        meetingInfoRepository.save(meetingInfo1);
        room1.fixRoom(meetingInfo1);
        roomRepository.save(room1);
        MeetingInfo meetingInfo2 = MeetingInfoFixture.getMeetingInfo(after);
        meetingInfoRepository.save(meetingInfo2);
        room2.fixRoom(meetingInfo2);
        roomRepository.save(room2);
        MeetingInfo meetingInfo3 = MeetingInfoFixture.getMeetingInfo(today);
        meetingInfoRepository.save(meetingInfo3);
        room5.fixRoom(meetingInfo2);
        roomRepository.save(room5);

        //then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "5");
        mockMvc.perform(get("/api/v1/rooms/my/games")
                .header(AUTHORIZATION, accessToken)
                .params(params))
            .andExpect(jsonPath("$.currentPageNumber").value(0))
            .andExpect(jsonPath("$.size").value(5))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.roomsInfos.length()").value(3))
            .andExpect(jsonPath("$.roomsInfos.[0].id").value(room5.getId()))
            .andExpect(jsonPath("$.roomsInfos.[1].id").value(room3.getId()))
            .andExpect(jsonPath("$.roomsInfos.[2].id").value(room2.getId()));
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
        params.add("page", "0");
        params.add("size", "5");
        mockMvc.perform(get("/api/v1/rooms/my/end-games")
                .header(AUTHORIZATION, accessToken)
                .params(params))
            .andExpect(jsonPath("$.size").value(5))
            .andExpect(jsonPath("$.hasNext").value(true))
            .andExpect(jsonPath("$.roomsInfos.length()").value(5))
            .andExpect(jsonPath("$.roomsInfos.[0].fixTime").value(
                before.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))));
    }

    @Test
    @DisplayName("[사용자는 자신이 참여했던 모임을 조회할 수 있다.2]")
    void getMyEndGameTest1() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.of(2024, 2, 21, 20, 0, 0);
        LocalDateTime before = LocalDateTime.of(2024, 2, 20, 20, 0, 0);
        given(nowTime.get()).willReturn(now);
        MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(before);

        //방 생성
        for (int i = 0; i < 6; i++) {
            //방 생성
            Room room = RoomFixture.getRoom(Gender.UNION);
            roomRepository.save(room);

            //방 참가
            Participant participant = Participant.of(loginUser.getId(), room.getId(), true);
            participantRepository.save(participant);

            //모임 확정
            meetingInfoRepository.save(meetingInfo);
            room.fixRoom(meetingInfo);
            roomRepository.save(room);
        }

        //then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "1");
        params.add("size", "5");
        mockMvc.perform(get("/api/v1/rooms/my/end-games")
                .header(AUTHORIZATION, accessToken)
                .params(params))
            .andExpect(jsonPath("$.currentPageNumber").value(1))
            .andExpect(jsonPath("$.size").value(5))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.roomsInfos.length()").value(1))
            .andExpect(jsonPath("$.roomsInfos.[0].fixLine").value(meetingInfo.getLine()))
            .andExpect(jsonPath("$.roomsInfos.[0].fixStation").value(meetingInfo.getStation()))
            .andExpect(jsonPath("$.roomsInfos.[0].fixPlace").value(meetingInfo.getMeetingPlace()));
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
    @DisplayName("[사용자는 자신이 참여했던 모임을 조회할 때, 리뷰 반영 여부도 함께 응답한다]")
    void getMyEndGameTest3() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.of(2024, 2, 21, 20, 0, 0);
        LocalDateTime before = LocalDateTime.of(2024, 2, 20, 20, 0, 0);
        given(nowTime.get()).willReturn(now);

        User anotherUser = userRepository.save(UserFixture.getUserFixture2("providerId", "url"));

        //방 생성
        Room room = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room);
        Room room2 = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room2);

        //방 참가
        participantRepository.save(Participant.of(loginUser.getId(), room.getId(), true));
        participantRepository.save(Participant.of(anotherUser.getId(), room.getId(), false));

        participantRepository.save(Participant.of(loginUser.getId(), room2.getId(), true));
        participantRepository.save(Participant.of(anotherUser.getId(), room2.getId(), false));

        //모임 확정
        MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(before);
        meetingInfoRepository.save(meetingInfo);
        room.fixRoom(meetingInfo);
        roomRepository.save(room);
        MeetingInfo meetingInfo2 = MeetingInfoFixture.getMeetingInfo(before);
        meetingInfoRepository.save(meetingInfo2);
        room2.fixRoom(meetingInfo2);
        roomRepository.save(room2);

        //리뷰 등록
        Review review = ReviewFixture.getReviewFixture(loginUser.getId(), anotherUser.getId(),
            room.getId(), ReviewFixture.getEvaluationFixture());
        reviewRepository.save(review);

        //then
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "5");
        mockMvc.perform(get("/api/v1/rooms/my/end-games")
                .header(AUTHORIZATION, accessToken)
                .params(params))
            .andExpect(jsonPath("$.size").value(5))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.roomsInfos.length()").value(2))
            .andExpect(jsonPath("$.roomsInfos.[0].fixTime").value(
                before.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.roomsInfos[0].reviewCompleted").value(true))
            .andExpect(jsonPath("$.roomsInfos[1].reviewCompleted").value(false));
    }

    @Disabled
    @Test
    @DisplayName("[사용자는 모든 조건에 맞는 방을 필터링 하여 조회할 수 있다]")
    void getSearchRoom() throws Exception {
        //given
        for (int i = 0; i < 10; i++) {
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
        params.add("size", "10");
        params.add("station", station);
        params.add("time", "평일_오전");
        params.add("category", "파티게임");
        params.add("gender", "남성");

        mockMvc.perform(get("/api/v1/rooms/filter")
                .header(AUTHORIZATION, accessToken)
                .params(params))
            .andExpect(jsonPath("$.size").value(10))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.roomsInfos.length()").value(1));
    }

    @Disabled
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
            .andExpect(jsonPath("$.time").value(
                room.getDaySlot().getDescription() + " " + room.getTimeSlot().getDescription()))
            .andExpect(jsonPath("$.startTime").value(room.getStartTime()))
            .andExpect(jsonPath("$.subwayLine").value(room.getSubwayLine()))
            .andExpect(jsonPath("$.subwayStation").value(room.getSubwayStation()))
            .andExpect(jsonPath("$.place").value(room.getPlaceName()))
            .andExpect(jsonPath("$.minParticipants").value(3))
            .andExpect(jsonPath("$.maxParticipants").value(6))
            .andExpect(jsonPath("$.isFix").value("미확정"))
            .andExpect(jsonPath("$.isLeader").value(true))
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
            .andExpect(jsonPath("$.time").value(
                room.getDaySlot().getDescription() + " " + room.getTimeSlot().getDescription()))
            .andExpect(jsonPath("$.startTime").value(
                meetingInfo.getMeetingTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.subwayLine").value(meetingInfo.getLine()))
            .andExpect(jsonPath("$.subwayStation").value(meetingInfo.getStation()))
            .andExpect(jsonPath("$.place").value(meetingInfo.getMeetingPlace()))
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
            .andExpect(jsonPath("$.time").value(
                room.getDaySlot().getDescription() + " " + room.getTimeSlot().getDescription()))
            .andExpect(jsonPath("$.startTime").value(
                meetingInfo.getMeetingTime()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .andExpect(jsonPath("$.subwayLine").value(meetingInfo.getLine()))
            .andExpect(jsonPath("$.subwayStation").value(meetingInfo.getStation()))
            .andExpect(jsonPath("$.place").value(meetingInfo.getMeetingPlace()))
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
            "2호선",
            "강남역",
            "레드버튼"
        );
        Participant participant = Participant.of(loginUser.getId(), room.getId(), true);
        participantRepository.save(participant);

        given(nowTime.get()).willReturn(request.meetingTime().minusDays(1));
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
            () -> assertThat(meetingInfo.getPeopleCount()).isEqualTo(findRoom.getHeadCount()),
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
        LocalDateTime now = LocalDateTime.of(2024, 3, 19, 20, 0, 0);
        given(nowTime.get()).willReturn(now);

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
    @DisplayName("[방 성별 조건과 다르다면 입장할 수 없다]")
    void participantRoomTest3() throws Exception {
        //given
        Room room = Room.of(
            title,
            description,
            3,
            6,
            "사당역 레드버튼",
            "2호선",
            station,
            DaySlot.WEEKDAY,
            TimeSlot.AM,
            "20시 예정",
            20,
            29,
            "imageUrl",
            Gender.FEMALE,
            categories
        );
        roomRepository.save(room);

        User user = User.of("email",
            "name",
            "nickName",
            "provider",
            "providerId",
            "testURL",
            1998,
            AgeGroup.TWENTY,
            Gender.MALE);
        User savedUser = userRepository.save(user);

        Token token = tokenProvider.createToken(savedUser.getId(), Role.USER);

        LocalDateTime now = LocalDateTime.of(2024, 3, 19, 20, 0, 0);
        given(nowTime.get()).willReturn(now);

        //then
        mockMvc.perform(post("/api/v1/rooms/in/" + room.getId())
                .header(AUTHORIZATION, "Bearer " + token.accessToken()))
            .andExpect(
                (result) -> assertThat(result.getResolvedException()).getClass().isAssignableFrom(
                    ValidationException.class))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("[방 연령 조건과 다르다면 입장할 수 없다]")
    void participantRoomTest4() throws Exception {
        //given
        Room room = Room.of(
            title,
            description,
            3,
            6,
            "사당역 레드버튼",
            "2호선",
            station,
            DaySlot.WEEKDAY,
            TimeSlot.AM,
            "20시 예정",
            20,
            29,
            "imageUrl",
            Gender.MALE,
            categories
        );
        roomRepository.save(room);

        User user = User.of("email",
            "name",
            "nickName",
            "provider",
            "providerId",
            "testURL",
            2020,
            AgeGroup.TWENTY,
            Gender.MALE);
        User savedUser = userRepository.save(user);

        Token token = tokenProvider.createToken(savedUser.getId(), Role.USER);

        LocalDateTime now = LocalDateTime.of(2024, 3, 19, 20, 0, 0);
        given(nowTime.get()).willReturn(now);

        //then
        mockMvc.perform(post("/api/v1/rooms/in/" + room.getId())
                .header(AUTHORIZATION, "Bearer " + token.accessToken()))
            .andExpect(
                (result) -> assertThat(result.getResolvedException()).getClass().isAssignableFrom(
                    ValidationException.class))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("[한 번 강퇴당한 방은 다시 입장할 수 없다]")
    void participantRoomTest5() throws Exception {
        //given
        Room room = Room.of(
            title,
            description,
            3,
            6,
            "사당역 레드버튼",
            "2호선",
            station,
            DaySlot.WEEKDAY,
            TimeSlot.AM,
            "20시 예정",
            20,
            29,
            "imageUrl",
            Gender.MALE,
            categories
        );
        roomRepository.save(room);

        User user = User.of("email",
            "name",
            "nickName",
            "provider",
            "providerId",
            "testURL",
            1998,
            AgeGroup.TWENTY,
            Gender.MALE);
        User savedUser = userRepository.save(user);

        blackListRepository.save(RoomBlackList.of(room.getId(), savedUser.getId()));

        Token token = tokenProvider.createToken(savedUser.getId(), Role.USER);

        LocalDateTime now = LocalDateTime.of(2024, 3, 19, 20, 0, 0);
        given(nowTime.get()).willReturn(now);

        //then
        mockMvc.perform(post("/api/v1/rooms/in/" + room.getId())
                .header(AUTHORIZATION, "Bearer " + token.accessToken()))
            .andExpect(
                (result) -> assertThat(result.getResolvedException()).getClass().isAssignableFrom(
                    ValidationException.class))
            .andExpect(status().is4xxClientError());
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
        roomRepository.save(room);
        Participant notLeader = Participant.of(user.getId(), room.getId(), false);
        participantRepository.save(notLeader);
        room.increaseHeadCount();
        roomRepository.save(room);

        //when
        mockMvc.perform(delete("/api/v1/rooms/" + room.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk());

        //then
        List<ParticipantJpaDto> participants = participantRepository.findParticipantByRoomId(
            room.getId());
        Optional<Room> optionalRoom = roomRepository.findById(room.getId());
        List<ChatMessage> chats = chatMessageRepository.findByRoomId(room.getId());

        assertThat(chats).isEmpty();
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
        boolean isBlackListExist = blackListRepository.existsByUserIdAndRoomId(notLeader.getId(),
            room.getId());

        assertThat(result).hasSize(1);
        assertThat(isBlackListExist).isTrue();
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
            .andExpect(
                (result) -> assertThat(result.getResolvedException()).getClass().isAssignableFrom(
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
            .andExpect(
                (result) -> assertThat(result.getResolvedException()).getClass().isAssignableFrom(
                    ValidationException.class))
            .andExpect(status().is4xxClientError());
    }
}