package com.civilwar.boardsignal.room.domain.repository;

import com.civilwar.boardsignal.room.domain.entity.Room;
import java.util.Collection;
import java.util.Optional;

public interface RoomRepository {

    Room save(Room room);

    void saveAll(Collection<Room> rooms);

    Optional<Room> findById(Long id);
}
