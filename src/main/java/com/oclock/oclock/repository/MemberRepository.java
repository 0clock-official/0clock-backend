package com.oclock.oclock.repository;

import com.oclock.oclock.dto.Major;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.MemberDto;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.model.Verification;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member join(MemberDto memberDto);

    void updateNickname(long memberId, String nickname);
    void updateChattingTime(long memberId, int chattingTime);
    int checkJoinStep(String email);
    void addMemberEmail(String email);
    void compareMemberEmailCode(String email, String code);
    void addMemberPassword(Member member);
    void addMemberPrivacy(Member member);
    Member selectMemberById(long id, RowMapper<Member> rowMapper);
    Member selectMemberByEmail(String email,RowMapper<Member> rowMapper);
    Member selectMemberByEmailAndPassword(String email, String password);
    List<Member> selectRandomMembers(Member requestMember);

    Member findByEmail(Email email);

    List<Verification> getVerification(String email);

    void insertVerification(String email, String verification);

    void updateVerification(String email, String verification);

    void certVerification(String email);

    boolean checkVerification(String email);

    void updateFcm(long memberId, String fcmToken);

    void deleteAccount(Long id);
}
