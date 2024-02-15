package com.civilwar.boardsignal.room.infrastructure.adaptor;

import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.infrastructure.repository.ParticipantJpaRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ParticipantRepositoryAdaptor implements ParticipantRepository {

    private final ParticipantJpaRepository participantJpaRepository;

    @Override
    public Participant save(Participant participant) {
        return participantJpaRepository.save(participant);
    }

    @Override
    public void saveAll(Collection<Participant> participants) {
        participantJpaRepository.saveAll(participants);
    }
}
