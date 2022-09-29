package com.oclock.oclock.service;

import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.ChattingTime;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.exception.OClockException;
import com.oclock.oclock.repository.ChattingRepository;
import com.oclock.oclock.repository.JdbcChattingRepository;
import com.oclock.oclock.repository.JdbcMemberRepository;
import com.oclock.oclock.repository.MemberRepository;
import com.oclock.oclock.secret.SecretTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
@Service
public class ChattingServiceImpl implements ChattingService {

    @Autowired
    private JdbcChattingRepository chattingRepository;
    @Autowired
    private JdbcMemberRepository memberRepository;
    @Autowired
    private SecretTool secretTool;
    @Override
    public void sendMessage(ChattingLog message) {
        secretTool.encryptChatting(message);
        chattingRepository.addChatting(message);
    }

    @Override
    public BigInteger randomMatching(Member requestMember) {
//        ChattingRoom.ChattingRoomBuilder builder = ChattingRoom.builder();
//        builder.createTime(Timestamp.valueOf(LocalDateTime.now()));
//        builder.member1(requestMember.getId());
//        List<Member> randomMembers = memberRepository.selectRandomMembers(requestMember);
//        int randomIndex = new Random().nextInt(2);
//        Member member = randomMembers.get(randomIndex);
//        builder.member2(member.getId());
//        builder.chattingTime(member.getChattingTime()); // 요청한 사람의 채팅시간보다 커야 서로 시간이 겹친다. 겹치는 시간중 가장 빠른 시간은 상대의 채팅시간이다.
//        ChattingRoom chattingRoom = builder.build();
//        return chattingRepository.createChattingRoom(chattingRoom);
        requestMember = memberRepository.selectMemberById(requestMember.getId());
        List<Member> randomMembers = memberRepository.selectRandomMembers(requestMember);
        if(randomMembers.isEmpty()){
            throw new OClockException();
        }else{
            int random = new Random().nextInt();
            random = random % (randomMembers.size()-1);
            Member selectedMember = randomMembers.get(random);
            int chattingTime = Math.max(requestMember.getChattingTime(), selectedMember.getChattingTime());
            ChattingRoom chattingRoom = ChattingRoom.builder()
                    .member1(requestMember.getId())
                    .member2(selectedMember.getId())
                    .chattingTime(chattingTime).build();
            return chattingRepository.createChattingRoom(chattingRoom);
        }
    }

    @Override
    public List<ChattingLog> getMonthlyChattingLogs(Member requestMember, ChattingRoom chattingRoom, Timestamp startTime) {
        LocalDateTime endTime = startTime.toLocalDateTime();
        endTime = endTime.plusMonths(1);
        List<ChattingLog> result= chattingRepository.selectChattingLogs(requestMember,chattingRoom,startTime,Timestamp.valueOf(endTime));
        secretTool.decryptChatting(result);
        return result;
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

    @Override
    public ChattingRoom getChattingRoom(Member requestMember, BigInteger chattingRoomId) {
        return chattingRepository.selectChattingRoom(requestMember,chattingRoomId);
    }

    @Override
    public ChattingRoom getChattingRoom(Member requestMember) {
        return chattingRepository.selectChattingRoom(requestMember);
    }
}
