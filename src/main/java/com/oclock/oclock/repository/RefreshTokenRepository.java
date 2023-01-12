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

    public List<RefreshToken> findByRefreshToken(String refreshToken) {
        String sql = "SELECT * FROM refreshToken WHERE refreshToken = ?";
        return jdbcTemplate.query(sql, new RefreshTokenMapper<>(), refreshToken);
    }

    public boolean existsById(long id) {
        String sql = "SELECT * FROM refreshToken WHERE id=?";
        return !jdbcTemplate.query(sql, (resultSet,rowNum) -> {return resultSet.getInt("id");}, id).isEmpty();
    }

    public int insertRefreshToken(long id, String refreshToken) {
        String sql = "INSERT INTO refreshToken (id, refreshToken) VALUES(?, ?)";
        return jdbcTemplate.update(sql, id, refreshToken);
    }

    public int updateRefreshToken(String refreshToken, long id) {
        String sql = "UPDATE refreshToken set refreshToken = ? where id = ?";
        return jdbcTemplate.update(sql, refreshToken, id);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM refreshToken WHERE id=?";
        jdbcTemplate.update(sql, id);
    }

}
