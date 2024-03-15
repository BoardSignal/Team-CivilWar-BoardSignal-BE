package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.Room;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomJpaRepository extends JpaRepository<Room, Long> {

    @EntityGraph(attributePaths = {"meetingInfo"})
    @Override
    Optional<Room> findById(Long roomId);

    // 내가 참여한 모든 room 조회 쿼리
    @EntityGraph(attributePaths = {"meetingInfo"})
    @Query("select r "
        + "from Room as r "
        + "join Participant as p "
        + "on r.id = p.roomId "
        + "where p.userId=:userId "
        + "and r.status='FIX'")
    List<Room> findMyFixRoom(@Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.id = :id")
    Optional<Room> findByIdWithLock(@Param("id") Long id);
}
