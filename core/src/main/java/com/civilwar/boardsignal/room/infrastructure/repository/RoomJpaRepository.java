package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.Room;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomJpaRepository extends JpaRepository<Room, Long> {

    @EntityGraph(attributePaths = {"meetingInfo"})
    @Override
    Optional<Room> findById(Long roomId);

    // 내가 현재 참여한 채팅방 조회
    // 미확정 모임 또는 모임 시간이 (오늘~미래)인 모임
    @Query("select r "
        + "from Room as r "
        + "left join MeetingInfo as m "
        + "on m.id = r.meetingInfo.id "
        + "join Participant as p "
        + "on r.id = p.roomId "
        + "where p.userId=:userId "
        + "and (m.meetingTime>=:today or m.meetingTime is null) "
        + "order by p.createdAt desc")
    Slice<Room> findMyChattingRoom(@Param("userId") Long userId,
        @Param("today") LocalDateTime today, Pageable pageable);

    // 내가 어제까지 참여한 종료된 모임 (페이징 버전)
    @Query("select r "
        + "from Room as r "
        + "left join MeetingInfo as m "
        + "on m.id = r.meetingInfo.id "
        + "join Participant as p "
        + "on r.id = p.roomId "
        + "where p.userId=:userId "
        + "and r.status='FIX' "
        + "and m.meetingTime<:today "
        + "order by m.meetingTime desc ")
    Slice<Room> findMyEndRoomPaging(@Param("userId") Long userId,
        @Param("today") LocalDateTime today, Pageable pageable);

    // 내가 어제까지 참여한 종료된 모임 갯수
    @Query("select count(r) "
        + "from Room as r "
        + "left join MeetingInfo as m "
        + "on m.id = r.meetingInfo.id "
        + "join Participant as p "
        + "on r.id = p.roomId "
        + "where p.userId=:userId "
        + "and r.status='FIX' "
        + "and m.meetingTime<:today "
        + "order by m.meetingTime desc ")
    int countByMyEndRoom(@Param("userId") Long userId, @Param("today") LocalDateTime today);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.id = :id")
    Optional<Room> findByIdWithLock(@Param("id") Long id);
}
