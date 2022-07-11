package com.oclock.oclock.repository;

import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.Member;

import java.util.List;

public interface ChattingRepository {
    void addChatting(ChattingLog chat);
    List<ChattingLog> selectChattingLogs(ChattingRoom chattingRoom);
    void createChattingRoom(ChattingRoom chattingRoom);
    void exitChattingRoom(Member member);
}
