package com.oclock.oclock.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.sql.Timestamp;

@Builder
@Getter
public class ChattingLog {
    private BigInteger id;
    private BigInteger chattingRoomId;
    private long sendMember;
    private long receiveMember;
    private Timestamp chattingTime;
    private String message;
}
