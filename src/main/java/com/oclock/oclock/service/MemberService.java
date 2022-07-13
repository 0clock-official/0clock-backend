package com.oclock.oclock.service;

import com.oclock.oclock.dto.Member;

import java.io.File;

public interface MemberService {
    int getJoinStep(String email); // 가입단계 확인
    // 각 가입 단계
    void joinStep1(String email);
    String joinStep2(String email, String code);
    void joinStep3(String token, String password);
    void joinStep4(File image);
    void joinStep5(Member member);
    //
    String[] login(Member member); // 로그인
    void updateMember(Member member); // 회원정보 수정
    void resetPassword(String email); // 비밀번호 리셋 요청
    void quitMember(Member member); // 회원탈퇴
}
