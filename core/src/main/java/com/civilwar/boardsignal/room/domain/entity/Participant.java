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
@Table(name = "PARTICIPANT_TABLE")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARTICIPANT_ID")
    private Long id;

    @Column(name = "PARTICIPANT_USER_ID")
    private Long userId;

    @Column(name = "PARTICIPANT_ROOM_ID")
    private Long roomId;

    @Column(name = "PARTICIPANT_IS_LEADER")
    private boolean isLeader;

    @Builder(access = AccessLevel.PRIVATE)
    private Participant(
        Long userId,
        Long roomId,
        boolean isLeader
    ) {
        this.userId = userId;
        this.roomId = roomId;
        this.isLeader = isLeader;
    }

    public static Participant of(
        Long userId,
        Long roomId,
        boolean isLeader
    ) {
        return Participant.builder()
            .userId(userId)
            .roomId(roomId)
            .isLeader(isLeader)
            .build();
    }
}
