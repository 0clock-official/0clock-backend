package com.oclock.oclock.service;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.model.Email;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public interface MemberService {
    // 각 가입 단계
    Member join(Email email, String password);

    int checkJoinStep(Email email);

    boolean checkEmail(Email email);

    Member findById(Long id);
    void joinWithToken(String token, String password);
    File joinStep4(File image);

    Boolean deleteAccount(Long id);
    Member login(Email email, String password); // 로그인
    void updateMember(Member member); // 회원정보 수정
    void resetPassword(String email); // 비밀번호 리셋 요청
}
