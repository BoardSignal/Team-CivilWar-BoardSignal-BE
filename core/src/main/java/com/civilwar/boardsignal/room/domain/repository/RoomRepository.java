package com.civilwar.boardsignal.room.domain.repository;

import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.dto.request.RoomSearchCondition;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RoomRepository {

    Room save(Room room);

    void saveAll(Collection<Room> rooms);

    Optional<Room> findById(Long id);

    List<Room> findMyFixRoom(Long userId);

    Slice<Room> findAll(RoomSearchCondition roomSearchCondition, Pageable pageable);

    Optional<Room> findByIdWithLock(Long id);

    void deleteById(Long id);
}
