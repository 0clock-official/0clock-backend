package com.oclock.oclock.repository;

import com.oclock.oclock.rowmapper.RefreshTokenMapper;
import com.oclock.oclock.security.RefreshToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class RefreshTokenRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    List<RefreshToken> findByRefreshToken(String refreshToken) {
        String sql = "SELECT * FROM refreshToken WHERE refresthToken = ?";
        return jdbcTemplate.query(sql, new RefreshTokenMapper<>(), refreshToken);
    }

    boolean existsByEmail(String email) {
        String sql = "SELECT * FROM refreshToken WHERE email=?";
        return !jdbcTemplate.query(sql, new RefreshTokenMapper<>(), email).isEmpty();
    }

    int insertRefreshToken(String email, String refreshToken) {
        String sql = "INSERT INTO refreshToken (email, refreshToken) VALUES(?, ?)";
        return jdbcTemplate.update(sql, email, refreshToken);
    }

    void deleteByEmail(String email) {
        String sql = "DELETE FROM refreshToken WHERE email=?";
        jdbcTemplate.update(sql, email);
    }

}
