package com.oclock.oclock.dto.response;

import com.oclock.oclock.dto.Member;
import lombok.Getter;

@Getter
public class OtherInfoDto {
    private String nickName;
    private int major;
    private int sex;
    public  OtherInfoDto(Member member){
        nickName = member.getNickName();
        major = member.getMajor();
        sex = member.getMemberSex();
    }
}
