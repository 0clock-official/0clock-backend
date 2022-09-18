package com.oclock.oclock.rowmapper;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.model.Email;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberRowMapper<T extends Member> implements RowMapper<T> {

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Member.MemberBuilder builder = Member.builder();
        builder.id(rs.getLong("id"))
                .memberSex(rs.getInt("memberSex"))
                .major(rs.getInt("major"))
                .chattingTime(rs.getInt("chattingTime"))
                .nickName(rs.getString("nickName"))
                .email(new Email(rs.getString("email")))
                .matchingSex(rs.getInt("matchingSex"));
        return (T) builder.build();
    }
}
