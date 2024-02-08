package com.civilwar.boardsignal.room.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "PARTICIPANT_TABLE")
public class Participant {

    private static final String PARTICIPANT = "participant";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARTICIPANT_ID")
    private Long id;

    @Column(name = "PARTICIPANT_USER_ID")
    private Long userId;

    @Column(name = "PARTICIPANT_ROOM_ID")
    private Long roomId;
}
