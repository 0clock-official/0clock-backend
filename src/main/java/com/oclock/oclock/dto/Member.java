package com.oclock.oclock.dto;

import com.oclock.oclock.model.Email;
import com.oclock.oclock.security.Jwt;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Builder
public class Member {
    private long id;
    private Email email;
    private String password;
    @Setter
    private BigInteger chattingRoomId;
    private int chattingTime;
    private int memberSex;
    private int matchingSex;
    private int major;
    private String nickName;
    private int joinStep;

    public static class MemberSex{
        public static final int MALE = 1;
        public static final int FEMALE = 2;
    }
    public static class MatchingSex{
        public static final int MALE = 1;
        public static final int FEMALE = 2;
        public static final int ALL = 3;
    }
    public static class JoinStep{
        public static final int START = 1;
        public static final int EMAIL_CERT = 2;
        public static final int SETTING_PASSWORD = 3;
        public static final int SEND_STUDENT_CARD = 4;
        public static final int SETTING_PRIVACY = 5;
        public static final int END = 6;
    }

    public String newApiToken(Jwt jwt, String[] roles) {
        Jwt.Claims claims = Jwt.Claims.of(id, nickName, email, roles);
        return jwt.newToken(claims);
    }
}
