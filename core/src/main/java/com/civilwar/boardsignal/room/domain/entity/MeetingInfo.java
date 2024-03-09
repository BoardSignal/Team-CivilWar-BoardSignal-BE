package com.civilwar.boardsignal.room.domain.entity;

import com.civilwar.boardsignal.room.domain.constants.WeekDay;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

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

    @Column(name = "MEETING_INFO_MEETING_TIME")
    private LocalDateTime meetingTime;

    @Column(name = "MEETING_INFO_WEEK_DAY")
    private WeekDay weekDay;

    @Column(name = "MEETING_INFO_PEOPLE_COUNT")
    private int peopleCount;

    @Column(name = "MEETING_INFO_LINE")
    private String line;

    @Column(name = "MEETING_INFO_STATION")
    private String station;

    @Column(name = "MEETING_INFO_MEETING_PLACE")
    private String meetingPlace;

    @Builder(access = AccessLevel.PRIVATE)
    public MeetingInfo(
        @NonNull LocalDateTime meetingTime,
        @NonNull String weekDay,
        @NonNull int peopleCount,
        @NonNull String line,
        @NonNull String station,
        @NonNull String meetingPlace
    ) {
        WeekDay weekDayInfo = WeekDay.of(weekDay);

        this.meetingTime = meetingTime;
        this.weekDay = weekDayInfo;
        this.peopleCount = peopleCount;
        this.line = line;
        this.station = station;
        this.meetingPlace = meetingPlace;
    }

    public static MeetingInfo of(
        LocalDateTime meetingTime,
        String weekday,
        int peopleCount,
        String line,
        String station,
        String meetingPlace
    ) {
        return MeetingInfo.builder()
            .meetingTime(meetingTime)
            .weekDay(weekday)
            .peopleCount(peopleCount)
            .line(line)
            .station(station)
            .meetingPlace(meetingPlace)
            .build();
    }
}
