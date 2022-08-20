package com.oclock.oclock.service;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.model.Email;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public interface MemberService {
    int getJoinStep(Email email); // 가입단계 확인
    // 각 가입 단계
    Member join(Email email, String password);

    boolean checkEmail(Email email);
    void joinWithToken(String token, String password);
    Optional<File> joinStep4(File image);
    Member joinAsMember(Member member);
    Member login(Email email, String password); // 로그인
    void updateMember(Member member); // 회원정보 수정
    void resetPassword(String email); // 비밀번호 리셋 요청
    void closeAccount(Member member); // 회원탈퇴
}
