package com.oclock.oclock.service;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{

    //private final PasswordEncoder passwordEncoder;

    @Autowired
    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public int getJoinStep(Email email) {
        return 0;
    }

    @Override
    public Member join(Email email, String password) {
        return null;
    }

    @Override
    public void joinWithToken(String token, String password) {

    }

    @Override
    public Optional<File> joinStep4(File image) {
        return Optional.empty();
    }

    @Override
    public Member joinAsMember(Member member) {
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

    @Override
    public void closeAccount(Member member) {

    }
}
