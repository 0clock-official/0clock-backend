package com.oclock.oclock.service;

import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.MemberDto;
import com.oclock.oclock.exception.NotFoundException;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.model.Verification;
import com.oclock.oclock.repository.MemberRepository;
import com.oclock.oclock.repository.RefreshTokenRepository;
import com.oclock.oclock.rowmapper.MemberRowMapperNoEmailAndChattingRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Member join(MemberDto memberDto) {
        return memberRepository.join(memberDto);
    }

    @Override
    public void editMyself(long memberId,Map<String, String> body) {
        if (body.get("nickname") != null && body.get("nickname") != "") {
            memberRepository.updateNickname(memberId,body.get("nickname"));
        }
        if (body.get("chattingTime") != null && body.get("chattingTime") != "") {
            memberRepository.updateChattingTime(memberId,Integer.parseInt(body.get("chattingTime")));
        }
    }

    @Override
    public void updateFcm(long memberId,String fcmToken) {
        memberRepository.updateFcm(memberId,fcmToken);
    }
    
    //TODO 학생증 업로드 구현하기
    @Override
    public void updateEmailStudentCard(Map<String, String> body) {

    }

    @Override
    public boolean checkEmail(Email email) {
        RowMapper<Member> rowMapper = new MemberRowMapperNoEmailAndChattingRoom<>();
        return memberRepository.selectMemberByEmail(email.getAddress(), rowMapper) == null;
    }

    @Override
    public Member findById(Long id,RowMapper<Member> rowMapper) {
        checkArgument(id != null, "Id must be provided.");

        return memberRepository.selectMemberById(id,rowMapper);
    }

    @Override
    public Member findByEmail(Email email) {
        checkArgument(email != null, "email must be provided.");
        return memberRepository.findByEmail(email);
    }


    @Override
    public Boolean deleteAccount(Long id) {
        return null;
    }

    @Override
    public Member login(Email email, String password) {
        checkArgument(password != null, "password must be provided.");

        Member member = findByEmail(email);
        member.login(passwordEncoder, password);
        return member;
    }

    @Override
    public List<Member> getMembers() {
        List<Member> members = memberRepository.getMembers();
        return members;
    }

    @Override
    public boolean checkVerification(String email, String verification) {
        List<Verification> verifications = memberRepository.getVerification(email);
        if (verifications.size() != 1) return false;
        return verifications.get(0).getVerification().equals(verification);
    }

    @Override
    public void renewVerification(String email, String verification) {
        List<Verification> verifications = memberRepository.getVerification(email);
        if (!verifications.isEmpty()) {
            memberRepository.updateVerification(email, verification);
            return;
        }
        memberRepository.insertVerification(email, verification);

    }

    @Override
    public String createRandomCode() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("NativePRNG");
        int randomInt = secureRandom.nextInt(100000);
        return Integer.toString(randomInt);
    }

    //TODO 회원정보 수정 
    @Override
    public void updateMember(Member member) {

    }
    //TODO 패스워드 재설정
    @Override
    public void resetPassword(String email) {

    }
    @Override
    public void mergeToken(long id, String verification) {
        if (refreshTokenRepository.existsById(id)) {
            refreshTokenRepository.updateRefreshToken(verification, id);
        }
        else refreshTokenRepository.insertRefreshToken(id,verification);
    }
}
