package com.civilwar.boardsignal.room;

import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.dto.mapper.RoomMapper;
import com.civilwar.boardsignal.room.dto.request.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.response.CreateRoomRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomFixture {

    public static Room getRoom() {
        return RoomMapper.toRoom(getCreateRoomRequest());
    }

    public static CreateRoomRequest getCreateRoomRequest() {
        return new CreateRoomRequest(
            "무슨무슨 모임입니다!",
            "재밌게 하려고 모집중",
            3,
            6,
            "주말",
            "오전",
            "이수역",
            21,
            25,
            "7호선",
            "이수역",
            "레드버튼 사당점",
            List.of("가족게임", "컬렉터블게임")
        );
    }

    public static CreateRoomResponse getCreateRoomResponse() {
        return new CreateRoomResponse(1L);
    }

    public static Participant getParticipant() {
        return Participant.of(1L, 1L);
    }

}
