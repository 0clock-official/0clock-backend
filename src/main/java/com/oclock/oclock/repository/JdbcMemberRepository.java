package com.oclock.oclock.repository;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.exception.OClockException;
import com.oclock.oclock.rowmapper.MemberRowMapperNoEmailAndChattingRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Random;

public class JdbcMemberRepository implements MemberRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void addMemberEmail(String email) {
        String sql = "insert into emailCode values(?,?)";
        int randomInt = new Random().nextInt(90000000)+10000000;
        jdbcTemplate.update(sql,email, randomInt+"");
    }

    @Override
    public void compareMemberEmailCode(String email, String code) {
        String sql = "select count(*) from emailCode where email = ? and code = ?";
        int result = jdbcTemplate.query(sql,(rs,rowNum)-> rs.getInt(1),email,code).get(0);
        if(result!=1){
            throw new OClockException();
        }
    }

    @Override
    public void addMemberPassword(Member member) {
        String sql = "update set password = ? where email = ?";
        jdbcTemplate.update(sql,member.getPassword(),member.getEmail());
    }

    @Override
    public void addMemberPrivacy(Member member) {
        String sql = "update set memberSex = ?, matchingSex = ?, major = ?, mickName = ?, chattingTime = ? where id = ?";
        jdbcTemplate.update(sql,member.getMemberSex(),member.getMatchingSex(),member.getMajor(),member.getNickName(),member.getChattingTime());
    }

    @Override
    public Member selectMemberById(long id) {
        String sql = "select * from member where id = ?";
        return jdbcTemplate.query(sql, new MemberRowMapperNoEmailAndChattingRoom<Member>(),id).get(0);
    }

    @Override
    public Member selectMemberByEmail(String email) {
        String sql = "select * from member where email = ?";
        return jdbcTemplate.query(sql, new MemberRowMapperNoEmailAndChattingRoom<Member>(),email).get(0);
    }

    @Override
    public Member selectMemberByEmailAndPassword(String email, String password) {
        String sql = "select * from member where email = ? and password = ?";
        return jdbcTemplate.query(sql, new MemberRowMapperNoEmailAndChattingRoom<Member>(),email).get(0);
    }

    @Override
    public List<Member> selectRandomMembers(Member requestMember) {
        String sql = "select * from member where chattingRoomId = null and chattingTime-? <=2 and memberSex = ? and (matchingSex = ? or matchingSex = 3) and major = ? order by rand() limit 0,3";
        if(requestMember.getMatchingSex() == Member.MatchingSex.ALL){
            sql = sql.replace(" and memberSex = ?","");
            return jdbcTemplate.query(sql,new MemberRowMapperNoEmailAndChattingRoom<>(),requestMember.getChattingTime(),requestMember.getMemberSex(),requestMember.getMajor());
        }
        return jdbcTemplate.query(sql,new MemberRowMapperNoEmailAndChattingRoom<>(),requestMember.getChattingTime(),requestMember.getMatchingSex(),requestMember.getMemberSex(),requestMember.getMajor());
    }

    @Override
    public List<Long> selectRandomMemberIds(Member requestMember) {
        String sql = "select id from member where chattingRoomId = null and chattingTime-? <=2 and memberSex = ? and (matchingSex = ? or matchingSex = 3) and major = ? order by rand() limit 0,3";
        if(requestMember.getMatchingSex() == Member.MatchingSex.ALL){
            sql = sql.replace(" and memberSex = ?","");
            return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), requestMember.getChattingTime(), requestMember.getMemberSex(), requestMember.getMajor());
        }
        return jdbcTemplate.query(sql,(rs, rowNum) -> rs.getLong("id"),requestMember.getChattingTime(),requestMember.getMatchingSex(),requestMember.getMemberSex(),requestMember.getMajor());
    }
}

