package com.oclock.oclock.service;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

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
        return memberRepository.selectMemberByEmailAndPassword(email.toString(), password);
    }

    @Override
    public void updateMember(Member member) {

    }

    @Override
    public void resetPassword(String email) {

    }
}
