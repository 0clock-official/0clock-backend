package com.oclock.oclock.rowmapper;

import com.oclock.oclock.model.Verification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberVerifyRowMapper<T extends Verification> implements RowMapper {

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Verification.VerificationBuilder builder = Verification.builder();
        builder.memberEmail(rs.getString("memberEmail"))
                .verification(rs.getString("verification"));
        return (T) builder.build();
    }
}
