package com.oclock.oclock.repository;

import com.oclock.oclock.dto.Major;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.MemberDto;
import com.oclock.oclock.model.Email;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member join(MemberDto memberDto);

    void updateNickname(String nickname);
    void updateChattingTime(String chattingTime);
    int checkJoinStep(String email);
    void addMemberEmail(String email);
    void compareMemberEmailCode(String email, String code);
    void addMemberPassword(Member member);
    void addMemberPrivacy(Member member);
    Member selectMemberById(long id);
    Member selectMemberByEmail(String email);
    Member selectMemberByEmailAndPassword(String email, String password);
    List<Member> selectRandomMembers(Member requestMember);
    List<Long> selectRandomMemberIds(Member requestMember);

    Member findByEmail(Email email);

    List<Member> getMembers();
}
