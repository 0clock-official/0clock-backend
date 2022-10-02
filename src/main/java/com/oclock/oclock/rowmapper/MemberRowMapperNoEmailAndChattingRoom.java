package com.oclock.oclock.rowmapper;

import com.oclock.oclock.dto.Member;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberRowMapperNoEmailAndChattingRoom<T extends Member> implements RowMapper<T> {

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Member.MemberBuilder builder = Member.builder();
        builder.id(rs.getLong("id"))
                .memberSex(rs.getInt("memberSex"))
                .matchingSex(rs.getInt("matchingSex"))
                .major(rs.getInt("major"))
                .chattingTime(rs.getInt("chattingTime"))
                .nickName(rs.getString("nickName"))
                .fcmToken(rs.getString("fcmToken"));
        return (T) builder.build();
    }
}
