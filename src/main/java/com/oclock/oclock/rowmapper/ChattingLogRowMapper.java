package com.oclock.oclock.rowmapper;

import com.oclock.oclock.dto.ChattingLog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChattingLogRowMapper<T extends ChattingLog> implements RowMapper<T> {
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        ChattingLog.ChattingLogBuilder builder = ChattingLog.builder();
        builder.message(rs.getString("message"))
                .chattingTime(rs.getTimestamp("chattingTime"))
                .id(rs.getBigDecimal("id").toBigInteger())
                .chattingRoomId(rs.getBigDecimal("chattingRoomId").toBigInteger())
                .receiveMember(rs.getLong("receiveMember"))
                .sendMember(rs.getLong("sendMember"));
        return (T) builder.build();
    }
}
