package com.oclock.oclock.service;

import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.MemberDto;
import com.oclock.oclock.model.Email;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface MemberService {
    // 각 가입 단계
    Member join(MemberDto memberDto);

    void editMyself(long memberId, Map<String, String> body);

    void updateFcm(long memberId,String fcmToken);
    void updateEmailStudentCard(Map<String, String> body);

    boolean checkEmail(Email email);

    Member findById(Long id, RowMapper<Member> rowMapper);

    Member findByEmail(Email email);

    Boolean deleteAccount(Long id);
    Member login(Email email, String password); // 로그인
    void updateMember(Member member); // 회원정보 수정
    void resetPassword(String email); // 비밀번호 리셋 요청

    List<Member> getMembers();

    boolean checkVerification(String email, String verification);

    void renewVerification(String email, String verification);

    String createRandomCode() throws NoSuchAlgorithmException;

    void mergeToken(long id, String verification);
}
