package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.RoomCategory;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoomCategoryJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<RoomCategory> roomCategories) {
        String sql = "insert"
            + " into"
            + " room_category_table"
            + " (room_category_room_id, room_category_category)"
            + " values (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RoomCategory roomCategory = roomCategories.get(i);
                ps.setLong(1, roomCategory.getRoom().getId());
                ps.setObject(2, roomCategory.getCategory().toString());
            }

            @Override
            public int getBatchSize() {
                return roomCategories.size();
            }
        });
    }


}
