package com.oclock.oclock.service;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.exception.NotFoundException;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public Member join(Email email, String password) {
        return null;
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
    public void updateMember(Member member) {

    }

    @Override
    public void resetPassword(String email) {

    }
}
