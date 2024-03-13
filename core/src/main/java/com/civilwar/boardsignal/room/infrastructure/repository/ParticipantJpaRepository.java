package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface ParticipantJpaRepository extends JpaRepository<Participant, Long> {

    void deleteParticipantByUserIdAndRoomId(Long userId, Long roomId);

    @Modifying(clearAutomatically = true)
    void deleteParticipantsByRoomId(Long roomId);
}
