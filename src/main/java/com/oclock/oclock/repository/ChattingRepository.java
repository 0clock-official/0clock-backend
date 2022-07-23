package com.oclock.oclock.repository;

import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.Member;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

public interface ChattingRepository {
    void addChatting(ChattingLog chat);
    List<ChattingLog> selectChattingLogs(Member requestMember, ChattingRoom chattingRoom, Timestamp startTime, Timestamp endTime);
    BigInteger createChattingRoom(ChattingRoom chattingRoom);
    void exitChattingRoom(Member member);
    Member selectChattingMember(Member requestMember);
    void updateChattingRoomTime(Member requestMember);
    ChattingRoom selectChattingRoom(Member requestMember, BigInteger chattingRoomId);
}
