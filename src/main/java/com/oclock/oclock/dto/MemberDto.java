package com.oclock.oclock.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    String email;
    String password;
    String nickname;
    Integer major;
    String chattingTime;
    Integer memberSex;
    Integer matchingSex;
    String fcmToken;
}
