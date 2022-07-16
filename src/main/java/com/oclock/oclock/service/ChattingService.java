package com.oclock.oclock.service;

import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.Member;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

public interface ChattingService {
    void sendMessage(ChattingLog message); // 메세지 전송
    BigInteger randomMatching(Member requestMember); // 랜덤 상대와 채팅방 생성
    BigInteger matching(long requestMemberId, long guestMemberId); // 특정 두 멤버끼리 채팅방 생성
    List<Long> getRandomMemberIdList(Member requestMember); // 매칭 후보 3인 조회
    List<ChattingLog> getMonthlyChattingLogs(Member requestMember, ChattingRoom chattingRoom, Timestamp startTime); // 월별 채팅기록 불러오기
    Member getChattingMember(Member requestMember,ChattingRoom chattingRoom); // 채팅 상대방 프로필 보기. 과거 참여했던 채팅방도 가능. 기록보유중이라면
    Member getChattingMember(Member requestMember); // 채팅 상대방 프로필 보기. 현재 참여중인 채팅방만 가능.
    void exitChattingRoom(Member requestMember); // 채팅방 나가기
    void changeChattingTime(Member requestMember); // 채팅방 채팅 시간 변경
}
