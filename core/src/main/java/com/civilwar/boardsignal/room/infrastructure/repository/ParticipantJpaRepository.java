package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantJpaRepository extends JpaRepository<Participant, Long> {

    void deleteParticipantByUserIdAndRoomId(Long userId, Long roomId);

    void deleteParticipantsByRoomId(Long roomId);
}
