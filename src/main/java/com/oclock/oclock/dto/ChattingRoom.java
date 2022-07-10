package com.oclock.oclock.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.sql.Timestamp;

@Builder
@Getter
public class ChattingRoom {
    private BigInteger id;
    private int chattingTime;
    private Timestamp createTime;
    private Timestamp deleteTime;
}
