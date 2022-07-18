package com.oclock.oclock.repository;

import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.exception.OClockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.*;
import java.util.List;
@Repository
public class JdbcChattingRepository implements ChattingRepository{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public void addChatting(ChattingLog chat) {
        String sql = "insert into chattingLog(chattingRoomId,sendMember,receiveMember,message) (select ?,?,?,? from chattingRoom" +
                " where exists(select * from chattingRoom where chattingRoom.id = ? and ((member1 = ? and member2 = ?)or(member2 = ? and member1 = ?)) and deleteTime is null) limit 1)"; //  채팅방이 열린상태이고, 채팅의 두 참여자가 모두 해당 채팅방에 참여한 경우만 insert
        BigInteger chattingRoomId = chat.getChattingRoomId();
        long senderId = chat.getSendMember();
        long receiveId = chat.getReceiveMember();
        jdbcTemplate.update(sql,chattingRoomId,senderId,receiveId,chat.getMessage(),chattingRoomId,senderId,receiveId,senderId,receiveId);
    }

    @Override
    public List<ChattingLog> selectChattingLogs(Member requestMember, ChattingRoom chattingRoom, Timestamp startTime, Timestamp endTime) {
        String sql = "select * from chattingLog where chattingRoomId = ? and chattingTime >= ? and chattingTime <= ? and (sendMember = ? or receiveMember = ?)";
        return jdbcTemplate.query(sql,new ChattingLogRowMapper<>(),chattingRoom.getId(),startTime,endTime,requestMember.getId(),requestMember.getId());
    }

    @Override
    public BigInteger createChattingRoom(ChattingRoom chattingRoom) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into chattingRoom(chattingTime,member1,member2) select ?,?,? from chattingRoom where exists" +
                "(select * from member where id in (?,?) and chattingRoomId is null) limit 1"; // 현재 참여중인 채팅이 없어야만 가능.
        int chattingTime = chattingRoom.getChattingTime();
        long member1 = chattingRoom.getMember1();
        long member2 = chattingRoom.getMember2();
        jdbcTemplate.update(con -> {
            PreparedStatement preparedStatement = con.prepareStatement(sql, new String[]{"id"});
            preparedStatement.setInt(1,chattingTime);
            preparedStatement.setLong(2,member1);
            preparedStatement.setLong(3,member2);
            preparedStatement.setLong(4,member1);
            preparedStatement.setLong(5,member2);
            return preparedStatement;
        },keyHolder);
        long chattingRoomId = keyHolder.getKey().longValue();
        String sql2 = "update member set chattingRoomId = ? where id in (?,?)";
        jdbcTemplate.update(sql2,chattingRoomId,member1,member2);
        return BigInteger.valueOf(keyHolder.getKey().longValue());
    }

    @Override
    public void exitChattingRoom(Member member) {
        String sql = "update member set chattingRoomID = null where chattingRoomId = ?";
        jdbcTemplate.update(sql,member.getChattingRoomId());
    }

    @Override
    public Member selectChattingMember(Member requestMember) {
        String sql = "select * from member " +
                "where " +
                "id != ? " +
                "and " +
                "(" +
                "id = (select member2 from chattingRoom where id = (select chattingRoomId from member where id = ?)) " +
                "or " +
                "id = (select member1 from chattingRoom where id = (select chattingRoomId from member where id = ?))" +
                ")";
        long id = requestMember.getId();
        return jdbcTemplate.query(sql, new JdbcMemberRepository.MemberRowMapperNoEmailAndChattingRoom<Member>(),id,id,id).get(0);
    }

    @Override
    public void updateChattingRoomTime(Member requestMember) {

    }

    class ChattingLogRowMapper<T extends ChattingLog> implements RowMapper<T>{
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
}
