package com.oclock.oclock.dto.response;

import com.oclock.oclock.dto.Member;
import lombok.Getter;

@Getter
public class SelfInfoDto {
    private String email;
    private String nickName;
    private int major;
    private int chattingTime;
    private int memberSex;
    private int matchingSex;

    public SelfInfoDto(Member member){
        email = member.getEmail().getAddress();
        nickName = member.getNickName();
        major = member.getMajor();
        chattingTime = member.getChattingTime();
        memberSex = member.getMemberSex();
        matchingSex = member.getMatchingSex();
    }
}
