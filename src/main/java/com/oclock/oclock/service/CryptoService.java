package com.oclock.oclock.service;

import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.Member;

import java.util.List;

public interface CryptoService {
    String passwordHashing(Member member);
    ChattingLog encryptChatting(ChattingLog chattingLog);
    ChattingLog decryptChatting(ChattingLog chattingLog);
    List<ChattingLog> encryptChattings(List<ChattingLog> chattingLogs);
    List<ChattingLog> decryptChattings(List<ChattingLog> chattingLogs);
}
