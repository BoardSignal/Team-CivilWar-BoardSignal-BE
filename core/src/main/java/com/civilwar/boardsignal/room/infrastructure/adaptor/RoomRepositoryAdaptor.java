package com.civilwar.boardsignal.room.infrastructure.adaptor;

import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.infrastructure.repository.RoomJpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoomRepositoryAdaptor implements RoomRepository {

    private final RoomJpaRepository roomJpaRepository;

    @Override
    public Room save(Room room) {
        return roomJpaRepository.save(room);
    }

    @Override
    public void saveAll(Collection<Room> rooms) {
        roomJpaRepository.saveAll(rooms);
    }

    @Override
    public Optional<Room> findById(Long id) {
        return roomJpaRepository.findById(id);
    }

    @Override
    public List<Room> findMyGame(Long id) {
        return roomJpaRepository.findMyGame(id);
    }
}
