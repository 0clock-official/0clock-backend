package com.oclock.oclock.repository;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.MemberDto;
import com.oclock.oclock.error.ErrorCode;
import com.oclock.oclock.exception.NotFoundException;
import com.oclock.oclock.exception.OClockException;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.rowmapper.MemberRowMapper;
import com.oclock.oclock.rowmapper.MemberRowMapperNoEmailAndChattingRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Repository
public class JdbcMemberRepository implements MemberRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Member join(MemberDto memberDto) {
        String sql = "insert into member (email, password, nickname, major, chatting_time, sex, fcmToken) values(?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, memberDto.getEmail(), memberDto. getPassword(), memberDto.getNickname(), memberDto.getMajor(), memberDto.getChattingTime(), memberDto.getSex(), memberDto.getFcmToken());
        return selectMemberByEmail(memberDto.getEmail());
    }

    @Override
    public void updateNickname(String nickname) {

    }

    @Override
    public void updateChattingTime(String chattingTime) {

    }

    @Override
    public int checkJoinStep(String email) {
        return selectMemberByEmail(email).getJoinStep();
    }
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
        List<Member> members;
        try {
            members = jdbcTemplate.query(sql, new MemberRowMapperNoEmailAndChattingRoom<Member>(),id);
        } catch (Exception e) {
            final String msg = "해당 id의 유저가 없습니다. [id:" + id + "]";
            log.warn(msg);
            throw new NotFoundException(msg, ErrorCode.NOT_FOUND);
        }
        return members.get(0);
    }

    @Override
    public Member selectMemberByEmail(String email) {
        String sql = "select * from member where email = ?";
        List<Member> members;
        try {
            members = jdbcTemplate.query(sql, new MemberRowMapperNoEmailAndChattingRoom<Member>(),email);
        } catch (Exception e) {
            final String msg = "해당 이메일의 유저가 없습니다. [email:" + email + "]";
            log.warn(msg);
            throw new NotFoundException(msg, ErrorCode.NOT_FOUND);
        }
        return members.get(0);
    }

    @Override
    public Member selectMemberByEmailAndPassword(String email, String password) {
        String sql = "select * from member where email = ? and password = ?";
        List<Member> members;
        try {
            members = jdbcTemplate.query(sql, new MemberRowMapperNoEmailAndChattingRoom<Member>(),email);
        } catch (Exception e) {
            final String msg = "해당 이메일과 비밀번호를 가진 유저가 없습니다. [email:" + email + "] [password:" + password;
            log.warn(msg);
            throw new NotFoundException(msg, ErrorCode.NOT_FOUND);
        }
        return members.get(0);
    }

    @Override
    public List<Member> selectRandomMembers(Member requestMember) {
        String sql = "select * from member where chattingRoomId is null and chattingTime-? <=2 and memberSex = ? and (matchingSex = ? or matchingSex = 3) and major = ? order by rand() limit 0,3";
        if(requestMember.getMatchingSex() == Member.MatchingSex.ALL){
            sql = sql.replace(" and memberSex = ?","");
            return jdbcTemplate.query(sql,new MemberRowMapperNoEmailAndChattingRoom<>(),requestMember.getChattingTime(),requestMember.getMemberSex(),requestMember.getMajor());
        }
        return jdbcTemplate.query(sql,new MemberRowMapperNoEmailAndChattingRoom<>(),requestMember.getChattingTime(),requestMember.getMatchingSex(),requestMember.getMemberSex(),requestMember.getMajor());
    }

    @Override
    public List<Long> selectRandomMemberIds(Member requestMember) {
        String sql = "select id from member where chattingRoomId is null and chattingTime-? <=2 and memberSex = ? and (matchingSex = ? or matchingSex = 3) and major = ? order by rand() limit 0,3";
        if(requestMember.getMatchingSex() == Member.MatchingSex.ALL){
            sql = sql.replace(" and memberSex = ?","");
            return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), requestMember.getChattingTime(), requestMember.getMemberSex(), requestMember.getMajor());
        }
        return jdbcTemplate.query(sql,(rs, rowNum) -> rs.getLong("id"),requestMember.getChattingTime(),requestMember.getMatchingSex(),requestMember.getMemberSex(),requestMember.getMajor());
    }

    @Override
    public Member findByEmail(Email email) {
        String sql = "SELECT * FROM member WHERE email=?";
        List<Member> members;
        try {
            members = jdbcTemplate.query(sql, new MemberRowMapper(), email.getAddress());
            log.info("The size of members is " + Integer.toString(members.size()));
        } catch (Exception e) {
            final String msg = "해당 이메일의 유저가 없습니다. [email:" + email.getAddress() + "]";
            log.warn(msg);
            throw new NotFoundException(msg, ErrorCode.NOT_FOUND);
        }
        return members.get(0);
    }

    @Override
    public List<Member> getMembers() {
        String sql = "SELECT * FROM member";
        List<Member> members;

        members = jdbcTemplate.query(sql, new MemberRowMapper<>());

        return members;
    }
}

