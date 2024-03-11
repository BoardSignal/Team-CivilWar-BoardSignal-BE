package com.civilwar.boardsignal.room.infrastructure.adaptor;

import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import com.civilwar.boardsignal.room.infrastructure.repository.ParticipantJpaRepository;
import com.civilwar.boardsignal.room.infrastructure.repository.ParticipantQueryJpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ParticipantRepositoryAdaptor implements ParticipantRepository {

    private final ParticipantJpaRepository participantJpaRepository;
    private final ParticipantQueryJpaRepository participantQueryJpaRepository;

    @Override
    public Participant save(Participant participant) {
        return participantJpaRepository.save(participant);
    }

    @Override
    public void saveAll(Collection<Participant> participants) {
        participantJpaRepository.saveAll(participants);
    }

    @Override
    public List<Participant> findAll() {
        return participantQueryJpaRepository.findAll();
    }

    @Override
    public List<ParticipantJpaDto> findParticipantByRoomId(Long roomId) {
        return participantQueryJpaRepository.findParticipantByRoomId(roomId);
    }

    @Override
    public Optional<Participant> findByUserIdAndRoomId(Long userId, Long roomId) {
        return participantQueryJpaRepository.findByUserIdAndRoomId(userId, roomId);
    }

    @Override
    public boolean existsByUserIdAndRoomId(Long userId, Long roomId) {
        return participantQueryJpaRepository.existsByUserIdAndRoomId(userId, roomId);
    }

    @Override
    public void deleteByUserIdAndRoomId(Long userId, Long roomId) {
        participantJpaRepository.deleteParticipantByUserIdAndRoomId(userId, roomId);
    }

    @Override
    public void deleteParticipantsByRoomId(Long roomId) {
        participantJpaRepository.deleteParticipantsByRoomId(roomId);
    }

    @Override
    public void deleteById(Long id) {
        participantJpaRepository.deleteById(id);
    }
}
