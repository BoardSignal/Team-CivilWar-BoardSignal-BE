package com.civilwar.boardsignal.room.infrastructure.adaptor;

import com.civilwar.boardsignal.room.domain.entity.RoomBlackList;
import com.civilwar.boardsignal.room.domain.repository.RoomBlackListRepository;
import com.civilwar.boardsignal.room.infrastructure.repository.RoomBlackListJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoomBlackListRepositoryAdaptor implements RoomBlackListRepository {

    private final RoomBlackListJpaRepository blackListJpaRepository;

    @Override
    public void save(RoomBlackList blackList) {
        blackListJpaRepository.save(blackList);
    }

    @Override
    public void deleteByRoomId(Long roomId) {
        blackListJpaRepository.deleteByRoomId(roomId);
    }

    @Override
    public boolean existsByUserIdAndRoomId(Long userId, Long roomId) {
        return blackListJpaRepository.existsByUserIdAndRoomId(userId, roomId);
    }
}
