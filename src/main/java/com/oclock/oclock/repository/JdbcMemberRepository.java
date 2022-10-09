package com.oclock.oclock.repository;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.MemberDto;
import com.oclock.oclock.dto.response.ErrorMessage;
import com.oclock.oclock.error.ErrorCode;
import com.oclock.oclock.exception.NotFoundException;
import com.oclock.oclock.exception.OClockException;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.model.Verification;
import com.oclock.oclock.rowmapper.MemberRowMapper;
import com.oclock.oclock.rowmapper.MemberRowMapperNoEmailAndChattingRoom;
import com.oclock.oclock.rowmapper.MemberVerfiRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
        String sql = "insert into member (email, password, nickname, major, chattingTime, memberSex,matchingSex, fcmToken) values(?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, memberDto.getEmail(), memberDto. getPassword(), memberDto.getNickname(), memberDto.getMajor(), memberDto.getChattingTime(), memberDto.getMemberSex(),memberDto.getMatchingSex(), memberDto.getFcmToken());
        return selectMemberByEmail(memberDto.getEmail(),new MemberRowMapper<>());
    }

    @Override
    public void updateNickname(long memberId, String nickname) {
        String sql = "update member set nickName=? where id = ?";
        jdbcTemplate.update(sql,nickname,memberId);
    }

    @Override
    public void updateChattingTime(long memberId, int chattingTime) {
        String sql = "update member set chattingTime=? where id = ?";
        jdbcTemplate.update(sql,chattingTime,memberId);
    }

    @Override
    public int checkJoinStep(String email) {
        return selectMemberByEmail(email,new MemberRowMapper<>()).getJoinStep();
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
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .code(401)
                    .message("인증코드가 일치하지 않습니다.").build();
            throw new OClockException(errorMessage);
        }
    }

    @Override
    public void addMemberPassword(Member member) {
        String sql = "update member set password = ? where email = ?";
        jdbcTemplate.update(sql,member.getPassword(),member.getEmail());
    }

    @Override
    public void addMemberPrivacy(Member member) {
        String sql = "update member set memberSex = ?, matchingSex = ?, major = ?, mickName = ?, chattingTime = ? where id = ?";
        jdbcTemplate.update(sql,member.getMemberSex(),member.getMatchingSex(),member.getMajor(),member.getNickName(),member.getChattingTime());
    }

    @Override
    public Member selectMemberById(long id, RowMapper<Member> rowMapper) {
        String sql = "select * from member where id = ?";
        List<Member> members;
        try {
            members = jdbcTemplate.query(sql, rowMapper,id);
        } catch (Exception e) {
            final String msg = "해당 id의 유저가 없습니다. [id:" + id + "]";
            log.warn(msg);
            throw new NotFoundException(msg, ErrorCode.NOT_FOUND);
        }
        return members.get(0);
    }

    @Override
    public Member selectMemberByEmail(String email,RowMapper<Member> rowMapper) {
        String sql = "select * from member where email = ?";
        List<Member> members;
        try {
            members = jdbcTemplate.query(sql, rowMapper,email);
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
        String sql = "select * from member where chattingRoomId is null and chattingTime-? <=2 and memberSex = ? and (matchingSex = ? or matchingSex = 3) and major = ? and id != ? order by rand() limit 0,3";
        if(requestMember.getMatchingSex() == Member.MatchingSex.ALL){
            sql = sql.replace(" and memberSex = ?","");
            return jdbcTemplate.query(sql,new MemberRowMapper<>(),requestMember.getChattingTime(),requestMember.getMemberSex(),requestMember.getMajor(),requestMember.getId());
        }
        return jdbcTemplate.query(sql,new MemberRowMapper<>(),requestMember.getChattingTime(),requestMember.getMatchingSex(),requestMember.getMemberSex(),requestMember.getMajor(),requestMember.getId());
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

    @Override
    public List<Verification> getVerification(String email) {
        String sql = "SELECT * FROM memberVerification WHERE memberEmail = ?";
        List<Verification> verifications;
        verifications = jdbcTemplate.query(sql, new MemberVerfiRowMapper<>(),email);
        return verifications;
    }

    @Override
    public void insertVerification(String email, String verification) {
        String sql = "INSERT into memberVerification (memberEmail, verification) values(?,?)";
        jdbcTemplate.update(sql, email, verification);
    }

    @Override
    public void updateVerification(String email, String verification) {
        String sql = "UPDATE memberVerification set verification = ? where memberEmail = ?";
        jdbcTemplate.update(sql, verification, email);
    }

    @Override
    public void updateFcm(long memberId, String fcmToken) {
        String sql = "UPDATE member set fcmToken = ? where id = ?";
        jdbcTemplate.update(sql, fcmToken, memberId);
    }

    @Override
    public void deleteAccount(Long id) {
        String sql = "DELETE from member where id = ?";
        jdbcTemplate.update(sql, id);
    }
}

