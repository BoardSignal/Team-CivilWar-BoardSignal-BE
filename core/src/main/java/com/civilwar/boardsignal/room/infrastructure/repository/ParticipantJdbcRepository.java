package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.Participant;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ParticipantJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsert(List<Participant> participants) {

        String sql = "insert into participant_table "
            + "(participant_user_id, participant_room_id, participant_is_leader, participant_last_exit) "
            + "values (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Participant participant = participants.get(i);

                ps.setLong(1, participant.getUserId());
                ps.setLong(2, participant.getRoomId());
                ps.setNull(3, SqlTypeValue.TYPE_UNKNOWN);
                ps.setNull(4, SqlTypeValue.TYPE_UNKNOWN);
            }

            @Override
            public int getBatchSize() {
                return participants.size();
            }
        });
    }

}
