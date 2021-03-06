package com.oclock.oclock.service;

import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.exception.OClockException;
import com.oclock.oclock.repository.ChattingRepository;
import com.oclock.oclock.repository.MemberRepository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class ChattingServiceImpl implements ChattingService {

    private ChattingRepository chattingRepository;
    private MemberRepository memberRepository;

    @Override
    public void sendMessage(ChattingLog message) {
        chattingRepository.addChatting(message);
    }

    @Override
    public BigInteger randomMatching(Member requestMember) {
        ChattingRoom.ChattingRoomBuilder builder = ChattingRoom.builder();
        builder.chattingTime(requestMember.getChattingTime());
        builder.createTime(Timestamp.valueOf(LocalDateTime.now()));
        builder.member1(requestMember.getId());
        List<Member> randomMembers = memberRepository.selectRandomMembers(requestMember);
        int randomIndex = new Random().nextInt(2);
        builder.member2(randomMembers.get(randomIndex).getId());
        ChattingRoom chattingRoom = builder.build();
        return chattingRepository.createChattingRoom(chattingRoom);
    }

    @Override
    public BigInteger matching(long requestMemberId, long guestMemberId) {
        ChattingRoom.ChattingRoomBuilder builder = ChattingRoom.builder();
        builder.member1(requestMemberId);
        builder.member2(guestMemberId);
        builder.createTime(Timestamp.valueOf(LocalDateTime.now()));
        return chattingRepository.createChattingRoom(builder.build());
    }

    @Override
    public List<Long> getRandomMemberIdList(Member requestMember) {
        return memberRepository.selectRandomMemberIds(requestMember);
    }

    @Override
    public List<ChattingLog> getMonthlyChattingLogs(Member requestMember, ChattingRoom chattingRoom, Timestamp startTime) {
        LocalDateTime endTime = startTime.toLocalDateTime();
        endTime = endTime.plusMonths(1);
        return chattingRepository.selectChattingLogs(requestMember,chattingRoom,startTime,Timestamp.valueOf(endTime));
    }

    @Override
    public Member getChattingMember(Member requestMember, ChattingRoom chattingRoom) {
        long requestMemberId = requestMember.getId();
        if(chattingRoom.getMember1()==requestMemberId){
            return memberRepository.selectMemberById(chattingRoom.getMember2());
        }else if(chattingRoom.getMember2()==requestMemberId){
            return memberRepository.selectMemberById(chattingRoom.getMember1());
        }else{
            throw new OClockException();
        }
    }

    @Override
    public Member getChattingMember(Member requestMember) {
        return chattingRepository.selectChattingMember(requestMember);
    }

    @Override
    public void exitChattingRoom(Member requestMember) {
        chattingRepository.exitChattingRoom(requestMember);
    }

    @Override
    public void changeChattingTime(Member requestMember) {
        chattingRepository.updateChattingRoomTime(requestMember);
    }
}
