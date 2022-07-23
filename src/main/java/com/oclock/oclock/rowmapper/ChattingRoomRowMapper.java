package com.oclock.oclock.rowmapper;

import com.oclock.oclock.dto.ChattingRoom;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChattingRoomRowMapper<T extends ChattingRoom> implements RowMapper<T> {

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        ChattingRoom.ChattingRoomBuilder builder = ChattingRoom.builder();
        builder.chattingTime(rs.getInt("chattingTime"))
                .createTime(rs.getTimestamp("createTime"))
                .member2(rs.getLong("member2"))
                .member1(rs.getLong("member1"))
                .deleteTime(rs.getTimestamp("deleteTime"))
                .id(rs.getBigDecimal("id").toBigInteger());
        return (T) builder.build();
    }
}
