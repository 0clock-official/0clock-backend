package com.oclock.oclock.rowmapper;

import com.oclock.oclock.model.Verification;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberVerfiRowMapper<T extends Verification> implements RowMapper {

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Verification.VerificationBuilder builder = Verification.builder();
        builder.memberEmail(rs.getString("email"))
                .verification(rs.getString("verification"));
        return (T) builder.build();
    }
}
