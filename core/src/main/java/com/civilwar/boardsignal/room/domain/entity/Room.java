package com.civilwar.boardsignal.room.domain.entity;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

import com.civilwar.boardsignal.room.domain.constants.RoomStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "ROOM_TABLE")
public class Room {

    private static final String ROOM = "room";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_ID")
    private Long id;

    @Column(name = "ROOM_TITLE")
    private String title;

    @Column(name = "ROOM_MIN_PARTICIPANTS")
    private int minParticipants;

    @Column(name = "ROOM_MAX_PARTICIPANTS")
    private int maxParticipants;

    @Column(name = "ROOM_DESCRIPTION")
    private String description;

    @Column(name = "ROOM_STATUS")
    private RoomStatus status;

    @Column(name = "ROOM_PLACE_NAME")
    private String placeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_MEETING_INFO_ID", foreignKey = @ForeignKey(NO_CONSTRAINT))
    private MeetingInfo meetingInfo;

    @OneToMany(mappedBy = "room", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<RoomCategory> roomCategories = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<Subway> subways = new ArrayList<>();
}
