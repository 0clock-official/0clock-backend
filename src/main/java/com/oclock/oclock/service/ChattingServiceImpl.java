package com.oclock.oclock.service;

import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.ChattingTime;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.response.ErrorMessage;
import com.oclock.oclock.exception.OClockException;
import com.oclock.oclock.repository.ChattingRepository;
import com.oclock.oclock.repository.JdbcChattingRepository;
import com.oclock.oclock.repository.JdbcMemberRepository;
import com.oclock.oclock.repository.MemberRepository;
import com.oclock.oclock.rowmapper.MemberRowMapper;
import com.oclock.oclock.rowmapper.MemberRowMapperNoEmailAndChattingRoom;
import com.oclock.oclock.secret.SecretTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
@Service
public class ChattingServiceImpl implements ChattingService {

    @Autowired
    private ChattingRepository chattingRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private SecretTool secretTool;
    @Override
    public void sendMessage(ChattingLog message) {
        secretTool.encryptChatting(message);
        chattingRepository.addChatting(message);
    }

    @Override
    public BigInteger randomMatching(Member requestMember) {
        requestMember = memberRepository.selectMemberById(requestMember.getId(),new MemberRowMapper<>());
        List<Member> randomMembers = memberRepository.selectRandomMembers(requestMember);
        if (randomMembers.isEmpty()){
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .message("매칭가능한 회원이 없습니다.")
                    .code(404)
                    .build();
            throw new OClockException(errorMessage);
        }else{
            int random = new Random().nextInt(100000)+1;
            random = random % randomMembers.size();
            Member other = randomMembers.get(random);
            ChattingRoom chattingRoom = ChattingRoom.builder()
                    .member1(requestMember.getId())
                    .member2(other.getId())
                    .chattingTime(Math.max(requestMember.getChattingTime(),other.getChattingTime()))
                    .build();
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
        RowMapper<Member> rowMapper = new MemberRowMapperNoEmailAndChattingRoom<>();
        if(chattingRoom.getMember1()==requestMemberId){
            return memberRepository.selectMemberById(chattingRoom.getMember2(),rowMapper);
        }else if(chattingRoom.getMember2()==requestMemberId){
            return memberRepository.selectMemberById(chattingRoom.getMember1(),rowMapper);
        }else{
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .code(401)
                    .message("자신이 참여한 채팅방만 참여자 조회가 가능합니다.").build();
            throw new OClockException(errorMessage);
        }
    }

    @Override
    public Member getChattingMember(Member requestMember) {
        try {
            return chattingRepository.selectChattingMember(requestMember);
        }catch (IndexOutOfBoundsException e){
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .code(409)
                    .message("참여중인 채팅방이 없습니다.").build();
            throw new OClockException(errorMessage);
        }
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
