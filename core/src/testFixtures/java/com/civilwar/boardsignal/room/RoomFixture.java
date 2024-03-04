package com.civilwar.boardsignal.room;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.common.MultipartFileFixture;
import com.civilwar.boardsignal.room.domain.constants.DaySlot;
import com.civilwar.boardsignal.room.domain.constants.TimeSlot;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.dto.mapper.RoomMapper;
import com.civilwar.boardsignal.room.dto.response.CreateRoomRequest;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoomFixture {

    public static Room getRoomWithMeetingInfo(LocalDateTime meetingTime, Gender gender)
        throws IOException {
        MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(meetingTime);
        Room room = getRoom(gender);
        room.fixRoom(meetingInfo);

        return room;
    }

    public static Room getRoom(Gender allowedGender) throws IOException {
        MockMultipartFile image = MultipartFileFixture.getMultipartFile();
        return RoomMapper.toRoom("imageUrl", getCreateRoomRequest(image), allowedGender);
    }

    public static Room getAnotherRoom(
        String title,
        String description,
        String station,
        DaySlot day,
        TimeSlot time,
        List<Category> categories,
        Gender allowedGender
    ) {
        return Room.of(
            title,
            description,
            3,
            6,
            "사당역 레드버튼",
            "2호선",
            station,
            day,
            time,
            "20시 예정",
            20,
            29,
            "imageUrl",
            allowedGender,
            categories
        );
    }

    public static CreateRoomRequest getCreateRoomRequest(MultipartFile image) {
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
            List.of("가족게임", "컬렉터블게임"),
            true,
            image

        );
    }

    public static Participant getParticipant() {
        return Participant.of(1L, 1L, true);
    }

}
