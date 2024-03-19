package com.civilwar.boardsignal.room.domain.repository;

import com.civilwar.boardsignal.room.domain.entity.RoomBlackList;

public interface RoomBlackListRepository {

    void save(RoomBlackList blackList);

    void deleteByRoomId(Long roomId);

    boolean existsByUserIdAndRoomId(Long userId, Long roomId);

}
