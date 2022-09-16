package com.oclock.oclock.service;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.MemberDto;
import com.oclock.oclock.exception.NotFoundException;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public Member join(MemberDto memberDto) {
        return memberRepository.join(memberDto);
    }

    @Override
    public void editMyself(Map<String, String> body) {
        if (body.get("nickname") != null && body.get("nickname") != "") {
            memberRepository.updateNickname(body.get("nickname"));
        }
        if (body.get("chattingTime") != null && body.get("chattingTime") != "") {
            memberRepository.updateChattingTime(body.get("chattingTime"));
        }
    }

    @Override
    public void updateFcm(Map<String, String> body) {

    }

    @Override
    public void updateEmailStudentCard(Map<String, String> body) {

    }

    @Override
    public int checkJoinStep(Email email) {
        return memberRepository.checkJoinStep(email.toString());
    }

    @Override
    public boolean checkEmail(Email email) {
        return false;
    }

    @Override
    public Member findById(Long id) {
        checkArgument(id != null, "Id must be provided.");

        return memberRepository.selectMemberById(id);
    }

    @Override
    public Member findByEmail(Email email) {
        checkArgument(email != null, "email must be provided.");
        return memberRepository.findByEmail(email);
    }

    @Override
    public void joinWithToken(String token, String password) {

    }

    @Override
    public File joinStep4(File image) {
        return image;
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
    public Member other(Long id) {
        return null;
    }

    @Override
    public void updateMember(Member member) {

    }

    @Override
    public void resetPassword(String email) {

    }
}
