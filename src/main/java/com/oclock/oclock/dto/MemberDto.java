package com.oclock.oclock.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
    String email;
    String password;
    String nickname;
    Integer major;
    Integer chattingTime;
    Integer sex;
    Integer matchingSex;
    String fcmToken;

    public MemberDto(String email, String password, String nickname, Integer major, Integer chattingTime, Integer sex, Integer matchingSex, String fcmToken) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.major = major;
        this.chattingTime = chattingTime;
        this.sex = sex;
        this.matchingSex = sex == 1 ? 2 : 1;
        this.fcmToken = fcmToken;
    }
}
