package com.civilwar.boardsignal.room.domain.repository;

import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.dto.request.RoomSearchCondition;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RoomRepository {

    Room save(Room room);

    void saveAll(Collection<Room> rooms);

    Optional<Room> findById(Long id);

    Slice<Room> findMyChattingRoom(Long userId, LocalDateTime today, Pageable pageable);

    Slice<Room> findMyEndRoomPaging(Long userId, LocalDateTime today, Pageable pageable);

    int countByMyEndRoom(Long userId, LocalDateTime today);

    Slice<Room> findAll(RoomSearchCondition roomSearchCondition, Pageable pageable);

    Optional<Room> findByIdWithLock(Long id);

    void deleteById(Long id);
}
