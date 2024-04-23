package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.Room;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoomJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<Room> rooms) {
        String sql = "insert"
            + " into"
            + " room_table"
            + " (room_allowed_gender, created_at, room_day_slot, room_description, room_head_count,"
            + " room_image_url, room_max_age, room_max_participants, room_meeting_info_id, room_min_age,"
            + " room_min_participants, room_place_name, room_start_time, room_status, room_subway_line,"
            + " room_subway_station, room_time_slot, room_title, updated_at)"
            + " values"
            + " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Room room = rooms.get(i);

                Long meetingInfoId = null;
                if (i%10==9) meetingInfoId = i/10L+1L;

                ps.setObject(1, room.getAllowedGender().toString());
                ps.setObject(2, LocalDateTime.now());
                ps.setObject(3, room.getDaySlot().toString());
                ps.setObject(4, room.getDescription());
                ps.setLong(5, room.getHeadCount());
                ps.setObject(6, room.getImageUrl());
                ps.setObject(7, room.getMaxAge());
                ps.setObject(8, room.getMaxParticipants());
                if(meetingInfoId==null) {
                    ps.setNull(9, SqlTypeValue.TYPE_UNKNOWN);
                }
                else{
                    ps.setLong(9, meetingInfoId);
                }
                ps.setObject(10,room.getMinAge());
                ps.setObject(11,room.getMinParticipants());
                ps.setObject(12,room.getPlaceName());
                ps.setObject(13,room.getStartTime());
                ps.setObject(14,room.getStatus().toString());
                ps.setObject(15,room.getSubwayLine());
                ps.setObject(16,room.getSubwayStation());
                ps.setObject(17,room.getTimeSlot().toString());
                ps.setObject(18,room.getTitle());
                ps.setObject(19, LocalDateTime.now());
            }

            @Override
            public int getBatchSize() {
                return rooms.size();
            }
        });
    }

}
