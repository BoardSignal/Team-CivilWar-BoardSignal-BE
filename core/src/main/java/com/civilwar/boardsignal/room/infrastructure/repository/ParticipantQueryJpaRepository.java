package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipantQueryJpaRepository extends JpaRepository<Participant, Long> {

    @Query(
        "select new com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto("
            + "u.id, u.nickname, u.ageGroup, p.isLeader, u.mannerScore"
            + ") "
            + "from Participant as p "
            + "join User as u "
            + "on p.userId = u.id "
            + "where p.roomId = :roomId")
    List<ParticipantJpaDto> findParticipantByRoomId(@Param("roomId") Long roomId);

    Optional<Participant> findByUserIdAndRoomId(Long userId, Long roomId);

    boolean existsByUserIdAndRoomId(Long userId, Long roomId);
}
