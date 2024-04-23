package com.civilwar.boardsignal.test;

import com.civilwar.boardsignal.room.RoomEasyRandomFixture;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.infrastructure.repository.RoomJdbcRepository;
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

}
