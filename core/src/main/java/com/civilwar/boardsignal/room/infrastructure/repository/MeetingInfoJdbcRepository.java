package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MeetingInfoJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<MeetingInfo> meetingInfos) {
        String sql = "insert into meeting_info_table "
            + "(meeting_info_meeting_time, meeting_info_people_count, meeting_info_line, meeting_info_station, meeting_info_meeting_place) "
            + "values (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                MeetingInfo meetingInfo = meetingInfos.get(i);

                ps.setObject(1, meetingInfo.getMeetingTime());
                ps.setObject(2, meetingInfo.getPeopleCount());
                ps.setObject(3, meetingInfo.getLine());
                ps.setObject(4, meetingInfo.getStation());
                ps.setObject(5, meetingInfo.getMeetingPlace());
            }

            @Override
            public int getBatchSize() {
                return meetingInfos.size();
            }
        });
    }

}
