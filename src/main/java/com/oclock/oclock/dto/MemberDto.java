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
    String chattingTime;
    Integer sex;
    String fcmToken;
}
