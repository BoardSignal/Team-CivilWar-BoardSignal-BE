package com.civilwar.boardsignal.test;

import com.civilwar.boardsignal.room.MeetingInfoEasyRandomFixture;
import com.civilwar.boardsignal.room.RoomEasyRandomFixture;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.infrastructure.repository.MeetingInfoJdbcRepository;
import com.civilwar.boardsignal.room.infrastructure.repository.RoomJdbcRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test2")
public class TestData {

    @Autowired
    private RoomJdbcRepository roomJdbcRepository;

    @Autowired
    private MeetingInfoJdbcRepository meetingInfoJdbcRepository;

    @Test
    @Rollback(value = false)
    void setRoomData() {

        int dataCount = 200000;

        EasyRandom nonFixRoom = RoomEasyRandomFixture.getNonFix();
        EasyRandom fixRoom = RoomEasyRandomFixture.getFix();

        List<Room> rooms = new ArrayList<>();
        IntStream.range(0, dataCount)
            .parallel()
            .forEach(i -> {
                if(i%10==0) {
                    Room room = fixRoom.nextObject(Room.class);
                    rooms.add(room);
                }
                else {
                    Room room = nonFixRoom.nextObject(Room.class);
                    rooms.add(room);
                }
            });

        roomJdbcRepository.batchInsert(rooms);
    }

    @Test
    @Rollback(value = false)
    void setMeetingInfoData() {

        int dataCount = 100000;

        EasyRandom meetingInfo = MeetingInfoEasyRandomFixture.getMeetingInfo(
            LocalDate.of(2023, 3, 1),
            LocalDate.of(2024, 5,1)
        );

        List<MeetingInfo> meetingInfos = new ArrayList<>();
        IntStream.range(0, dataCount)
            .parallel()
            .forEach(i -> {
                MeetingInfo m = meetingInfo.nextObject(MeetingInfo.class);
                meetingInfos.add(m);
            });

        meetingInfoJdbcRepository.batchInsert(meetingInfos);
    }

}
