package com.civilwar.boardsignal.room.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.room.MeetingInfoFixture;
import com.civilwar.boardsignal.room.RoomFixture;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.request.ApiCreateRoomRequest;
import com.civilwar.boardsignal.room.infrastructure.repository.MeetingInfoJpaRepository;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private MeetingInfoJpaRepository meetingInfoRepository;
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
            Room room = RoomFixture.getRoom();
            roomRepository.save(room);

            //방 참가
            Participant participant = Participant.of(loginUser.getId(), room.getId(), true);
            participantRepository.save(participant);

            //모임 확정
            MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(before);
            meetingInfoRepository.save(meetingInfo);
            room.updateMeetingInfo(meetingInfo);
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
            Room room = RoomFixture.getRoom();
            roomRepository.save(room);

            //방 참가
            Participant participant = Participant.of(loginUser.getId(), room.getId(), true);
            participantRepository.save(participant);

            //모임 확정
            MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(after);
            meetingInfoRepository.save(meetingInfo);
            room.updateMeetingInfo(meetingInfo);
            roomRepository.save(room);
        }

        //non-fix 방 생성
        for (int i = 0; i < 5; i++) {
            //방 생성
            Room room = RoomFixture.getRoom();
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
}