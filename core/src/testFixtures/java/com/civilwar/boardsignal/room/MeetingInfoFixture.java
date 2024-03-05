package com.civilwar.boardsignal.room;

import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MeetingInfoFixture {

    public static MeetingInfo getMeetingInfo(LocalDateTime meetingTime) {
        return MeetingInfo.of(
            meetingTime,
            "토요일",
            5,
            "2호선",
            "사당역",
            "레드버튼"
        );
    }

}
