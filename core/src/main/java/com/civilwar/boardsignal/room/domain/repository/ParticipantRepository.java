package com.civilwar.boardsignal.room.domain.repository;

import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import java.util.Collection;
import java.util.List;

public interface ParticipantRepository {

    Participant save(Participant participant);

    void saveAll(Collection<Participant> participants);

    List<ParticipantJpaDto> findParticipantByRoomId(Long roomId);
}
