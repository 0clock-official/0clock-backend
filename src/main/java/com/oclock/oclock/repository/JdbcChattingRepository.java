package com.oclock.oclock.repository;

import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.exception.OClockException;
import com.oclock.oclock.rowmapper.ChattingLogRowMapper;
import com.oclock.oclock.rowmapper.ChattingRoomRowMapper;
import com.oclock.oclock.rowmapper.MemberRowMapperNoEmailAndChattingRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
        long senderId = chat.getSendMember();
        long receiveId = chat.getReceiveMember();
        String sql = "select id from chattingRoom where member1 in (?,?) and member2 in (?,?) and deleteTime is null";
        BigInteger selectedChattingRoomId = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            long number = rs.getLong(1);
            return BigInteger.valueOf(number);
        },senderId,receiveId,senderId,receiveId);
        BigInteger chattingRoomId = chat.getChattingRoomId();
        if(selectedChattingRoomId.equals(chattingRoomId)){
            String message = chat.getMessage();
            sql = "insert into chattingLog(chattingRoomId,sendMember,receiveMember,message) values(?,?,?,?)";
            jdbcTemplate.update(sql,chattingRoomId,senderId,receiveId,message);
        }else{
            throw new OClockException();
        }
    }

    @Override
    public List<ChattingLog> selectChattingLogs(Member requestMember, ChattingRoom chattingRoom, Timestamp startTime, Timestamp endTime) {
        String sql = "select * from chattingLog where chattingRoomId = ? and chattingTime >= ? and chattingTime <= ? and (sendMember = ? or receiveMember = ?)";
        return jdbcTemplate.query(sql,new ChattingLogRowMapper<>(),chattingRoom.getId(),startTime,endTime,requestMember.getId(),requestMember.getId());
    }

    @Override
    public BigInteger createChattingRoom(ChattingRoom chattingRoom) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "insert into chattingRoom(chattingTime,member1,member2) select ?,?,? from member where exists" +
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
        sql = "update chattingRoom set deleteTime = current_timestamp() where id =?";
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
        return jdbcTemplate.query(sql, new MemberRowMapperNoEmailAndChattingRoom<Member>(),id,id,id).get(0);
    }

    @Override
    public void updateChattingRoomTime(Member requestMember) {

    }

    @Override
    public ChattingRoom selectChattingRoom(Member requestMember, BigInteger chattingRoomId) {
        String sql = "select * from chattingRoom where id = ? and ? in (member1,member2)";
        return jdbcTemplate.queryForObject(sql,new ChattingRoomRowMapper<ChattingRoom>(),chattingRoomId,requestMember.getId());
    }

    @Override
    public ChattingRoom selectChattingRoom(Member requestMember) {
        String sql = "select * from chattingRoom where ? in (member1,member2) and deleteTime is null";
        return jdbcTemplate.queryForObject(sql,new ChattingRoomRowMapper<ChattingRoom>(),requestMember.getId());
    }
}
