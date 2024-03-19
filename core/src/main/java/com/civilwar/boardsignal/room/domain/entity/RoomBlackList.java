package com.civilwar.boardsignal.room.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "ROOM_BLACK_LIST")
public class RoomBlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_BLACK_LIST_ID")
    private Long id;

    @Column(name = "ROOM_BLACK_LIST_ROOM_ID")
    private Long roomId;

    @Column(name = "ROOM_BLACK_LIST_USER_ID")
    private Long userId;

    @Builder(access = AccessLevel.PRIVATE)
    private RoomBlackList(
        Long roomId,
        Long userId
    ) {
        this.roomId = roomId;
        this.userId = userId;
    }

    public static RoomBlackList of(
        Long roomId,
        Long userId
    ) {
        return RoomBlackList.builder()
            .roomId(roomId)
            .userId(userId)
            .build();
    }
}
