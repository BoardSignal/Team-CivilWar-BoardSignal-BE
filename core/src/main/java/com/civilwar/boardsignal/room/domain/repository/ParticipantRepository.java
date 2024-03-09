package com.civilwar.boardsignal.room.domain.repository;

import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ParticipantRepository {

    Participant save(Participant participant);

    void saveAll(Collection<Participant> participants);

    List<Participant> findAll();

    List<ParticipantJpaDto> findParticipantByRoomId(Long roomId);

    Optional<Participant> findByUserIdAndRoomId(Long userId, Long roomId);

    boolean existsByUserIdAndRoomId(Long userId, Long roomId);

    void deleteById(Long id);
}
