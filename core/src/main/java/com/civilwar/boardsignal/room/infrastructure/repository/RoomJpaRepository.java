package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.Room;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomJpaRepository extends JpaRepository<Room, Long> {

    // 내가 참여한 모든 room 조회 쿼리
    @EntityGraph(attributePaths = {"roomCategories", "meetingInfo"})
    @Query("select r "
        + "from Room as r "
        + "join Participant as p "
        + "on r.id = p.roomId "
        + "where p.userId=:userId "
        + "and r.status='FIX'")
    List<Room> findMyFixRoom(@Param("userId") Long userId);

}
