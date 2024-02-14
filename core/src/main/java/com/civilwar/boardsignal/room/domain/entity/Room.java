package com.civilwar.boardsignal.room.domain.entity;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.room.domain.constants.DaySlot;
import com.civilwar.boardsignal.room.domain.constants.RoomStatus;
import com.civilwar.boardsignal.room.domain.constants.TimeSlot;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "ROOM_TABLE")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_ID")
    private Long id;

    @Column(name = "ROOM_TITLE")
    private String title;

    @Column(name = "ROOM_DESCRIPTION")
    private String description;

    @Column(name = "ROOM_MIN_PARTICIPANTS")
    private int minParticipants;

    @Column(name = "ROOM_MAX_PARTICIPANTS")
    private int maxParticipants;

    @Column(name = "ROOM_STATUS")
    private RoomStatus status;

    @Column(name = "ROOM_PLACE_NAME")
    private String placeName;

    @Column(name = "ROOM_SUBWAY_LINE")
    private String subwayLine;

    @Column(name = "ROOM_SUBWAY_STATION")
    private String subwayStation;

    @Column(name = "ROOM_DAY_SLOT")
    private DaySlot daySlot;

    @Column(name = "ROOM_TIME_SLOT")
    private TimeSlot timeSlot;

    @Column(name = "ROOM_START_TIME")
    private String startTime;

    @Column(name = "ROOM_MIN_AGE")
    private int minAge;

    @Column(name = "ROOM_MAX_AGE")
    private int maxAge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_MEETING_INFO_ID", foreignKey = @ForeignKey(NO_CONSTRAINT))
    private MeetingInfo meetingInfo;

    @OneToMany(mappedBy = "room", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<RoomCategory> roomCategories = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Room(
        @NonNull String title,
        @NonNull String description,
        int minParticipants,
        int maxParticipants,
        @NonNull String placeName,
        @NonNull String subwayLine,
        @NonNull String subwayStation,
        @NonNull DaySlot daySlot,
        @NonNull TimeSlot timeSlot,
        @NonNull String startTime,
        int minAge,
        int maxAge,
        @NonNull List<Category> roomCategories
    ) {
        this.title = title;
        this.description = description;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.status = RoomStatus.NON_FIX;
        this.placeName = placeName;
        this.subwayLine = subwayLine;
        this.subwayStation = subwayStation;
        this.daySlot = daySlot;
        this.timeSlot = timeSlot;
        this.startTime = startTime;
        this.minAge = minAge;
        this.maxAge = maxAge;
        roomCategories.forEach(category -> {
            RoomCategory roomCategory = RoomCategory.of(this, category);
            this.roomCategories.add(roomCategory);
        });
    }

    public static Room of(
        String title,
        String description,
        int minParticipants,
        int maxParticipants,
        String placeName,
        String subwayLine,
        String subwayStation,
        DaySlot daySlot,
        TimeSlot timeSlot,
        String startTime,
        int minAge,
        int maxAge,
        List<Category> roomCategories
    ) {
        return Room.builder()
            .title(title)
            .description(description)
            .minParticipants(minParticipants)
            .maxParticipants(maxParticipants)
            .placeName(placeName)
            .subwayLine(subwayLine)
            .subwayStation(subwayStation)
            .daySlot(daySlot)
            .timeSlot(timeSlot)
            .startTime(startTime)
            .minAge(minAge)
            .maxAge(maxAge)
            .roomCategories(roomCategories)
            .build();
    }
}
