package com.oclock.oclock.repository;

import com.oclock.oclock.dto.Major;
import com.oclock.oclock.dto.Member;

import java.util.List;

public interface MemberRepository {
    void addMemberEmail(String email);
    void compareMemberEmailCode(String email, String code);
    void addMemberPassword(Member member);
    void addMemberPrivacy(Member member);
    Member selectMemberById(long id);
    Member selectMemberByEmail(String email);
    Member selectMemberByEmailAndPassword(String email, String password);
    List<Member> selectRandomMembers(Member requestMember);
    List<Long> selectRandomMemberIds(Member requestMember);
}
