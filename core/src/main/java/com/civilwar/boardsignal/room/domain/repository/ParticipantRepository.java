package com.civilwar.boardsignal.room.domain.repository;

import com.civilwar.boardsignal.room.domain.entity.Participant;
import java.util.Collection;

public interface ParticipantRepository {

    Participant save(Participant participant);

    void saveAll(Collection<Participant> participants);

}
