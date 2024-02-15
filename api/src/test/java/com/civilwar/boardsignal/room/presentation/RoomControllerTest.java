package com.civilwar.boardsignal.room.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.request.ApiCreateRoomRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@DisplayName("[RoomController 테스트]")
class RoomControllerTest extends ApiTestSupport {

    @Autowired
    private RoomRepository roomRepository;

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
            List.of("가족게임", "컬렉터블게임")
        );

        ResultActions resultActions = mockMvc.perform(post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
                .content(toJson(request)))
            .andExpect(status().isOk());

        Room room = roomRepository.findById(1L).orElseThrow();

        resultActions.andExpect(jsonPath("$.roomId").value(room.getId()));

    }
}