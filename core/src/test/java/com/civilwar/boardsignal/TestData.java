package com.civilwar.boardsignal;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.room.RoomEasyRandomFixture;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.entity.RoomCategory;
import com.civilwar.boardsignal.room.infrastructure.repository.RoomCategoryJdbcRepository;
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

    @Autowired
    private RoomCategoryJdbcRepository roomCategoryJdbcRepository;

    @Test
    @Rollback(value = false)
    void setRoomData() {

        int dataCount = 1;

        EasyRandom easyRandom = RoomEasyRandomFixture.get();

        List<Room> rooms = new ArrayList<>();
        List<RoomCategory> roomCategories = new ArrayList<>();
        IntStream.range(0, dataCount)
            .parallel()
            .forEach(i -> {
                Room room = easyRandom.nextObject(Room.class);
                RoomCategory roomCategory = RoomCategory.of(room, Category.PARTY);
                rooms.add(room);
                roomCategories.add(roomCategory);
            });


        roomJdbcRepository.batchInsert(rooms);
        roomCategoryJdbcRepository.batchInsert(roomCategories);
    }

}
