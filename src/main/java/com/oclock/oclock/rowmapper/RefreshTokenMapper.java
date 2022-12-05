package com.oclock.oclock.rowmapper;

import com.oclock.oclock.model.Verification;
import com.oclock.oclock.security.RefreshToken;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RefreshTokenMapper<T extends RefreshToken> implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        RefreshToken.RefreshTokenBuilder builder = RefreshToken.builder();
        builder.email(rs.getString("email"))
                .refreshToken(rs.getString("verification"));
        return (T) builder.build();
    }
}
