package com.civilwar.boardsignal.room.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "MEETING_INFO_TABLE")
public class MeetingInfo {

    private static final String MEETING_INFO = "meeting_info";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEETING_INFO_ID")
    private Long id;

    private LocalDateTime meetingTime;

    private int peopleCount;

    private String line;

    private String station;

    private String meetingPlace;
}
