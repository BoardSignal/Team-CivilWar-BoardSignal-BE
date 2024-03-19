package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.RoomBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface RoomBlackListJpaRepository extends JpaRepository<RoomBlackList, Long> {

    @Modifying(clearAutomatically = true)
    void deleteByRoomId(Long roomId);

    boolean existsByUserIdAndRoomId(Long userId, Long roomId);
}
