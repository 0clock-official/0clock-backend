package com.oclock.oclock.repository;

import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.ChattingTime;
import com.oclock.oclock.dto.Member;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

public interface ChattingRepository {
    void addChatting(ChattingLog chat);
    boolean canAddChatting(ChattingLog chattingLog);
    List<ChattingLog> selectChattingLogs(Member requestMember, ChattingRoom chattingRoom, Timestamp startTime, Timestamp endTime);
    BigInteger createChattingRoom(ChattingRoom chattingRoom);
    boolean canCreateChattingRoom(ChattingRoom chattingRoom);
    void exitChattingRoom(Member member);
    Member selectChattingMember(Member requestMember);
    void updateChattingRoomTime(Member requestMember, int chattingTime,ChattingRoom chattingRoom);
    void addChattingRoomTimeChangeRequest(Member member,int chattingTime, ChattingRoom chattingRoom);
    void deleteChattingRoomTimeChangeRequest(ChattingRoom chattingRoom);
    int selectChattingRoomTimeChangeRequest(ChattingRoom chattingRoom);
    ChattingRoom selectChattingRoom(Member requestMember, BigInteger chattingRoomId);
    ChattingRoom selectChattingRoom(Member requestMember);
}
