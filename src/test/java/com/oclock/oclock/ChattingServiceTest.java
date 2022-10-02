package com.oclock.oclock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.repository.JdbcMemberRepository;
import com.oclock.oclock.repository.MemberRepository;
import com.oclock.oclock.secret.SecretTool;
import com.oclock.oclock.service.ChattingServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChattingServiceTest {
    @Autowired
    ChattingServiceImpl chattingService;
    @Autowired
    JdbcMemberRepository memberRepository;
    @MockBean
    SecretTool secretTool;
//    @Test
//    @Order(1)
//    public void 채팅방선택생성및조회테스트(){
//        Member requestMember = memberRepository.selectMemberById(1);
//        List<Long> randomMemberIdList = chattingService.getRandomMemberIdList(requestMember);
//        int randomIndex = new Random().nextInt(2)%randomMemberIdList.size();
//        BigInteger chattingRoomId = chattingService.matching(requestMember.getId(),randomMemberIdList.get(randomIndex));
//        ChattingRoom chattingRoom = chattingService.getChattingRoom(requestMember,chattingRoomId);
//        ChattingRoom chattingRoom1 = chattingService.getChattingRoom(requestMember);
//        Assertions.assertThat(chattingRoom.getId()).isEqualTo(chattingRoom1.getId());
//        Assertions.assertThat(chattingRoom.getMember1()).isEqualTo(requestMember.getId());
//        Assertions.assertThat(chattingRoom.getMember2()).isEqualTo(randomMemberIdList.get(randomIndex));
//        Assertions.assertThat(chattingRoom.getChattingTime()).isBetween(requestMember.getChattingTime(), requestMember.getChattingTime()+2);
//        Member member = memberRepository.selectMemberById(chattingRoom.getMember2());
//        Assertions.assertThat(member.getChattingTime()).isEqualTo(chattingRoom.getChattingTime());
//        if(requestMember.getMatchingSex()==Member.MatchingSex.ALL){ // 내 매칭성별이 "모두" 일 경우
//            if(member.getMatchingSex()==Member.MatchingSex.ALL) { // 상대방의 매칭성별이 "모두"일 경우
//            }else{ // 상대방의 매칭성별이 "모두"가 아닌 경우
//                Assertions.assertThat(member.getMatchingSex()).isEqualTo(requestMember.getMemberSex());
//            }
//        }else {
//            Assertions.assertThat(requestMember.getMatchingSex()).isEqualTo(member.getMemberSex()); // 요청자의 매칭성별과 매칭된 상대방의 성별 비교
//            if(member.getMatchingSex()==Member.MatchingSex.ALL) { // 상대방의 매칭성별이 "모두"일 경우
//            }else{ // 상대방의 매칭성별이 "모두"가 아닌 경우
//                Assertions.assertThat(member.getMatchingSex()).isEqualTo(requestMember.getMemberSex());
//            }
//        }
//        Assertions.assertThat(requestMember.getMajor()).isEqualTo(member.getMajor());
//    }
//    @Test
//    @Order(2)
//    public void 채팅전송및조회테스트(){
//        Member requestMember = memberRepository.selectMemberById(1);
//        ChattingRoom chattingRoom = chattingService.getChattingRoom(requestMember);
//        long receiver = chattingRoom.getMember1()==requestMember.getId() ? chattingRoom.getMember2():chattingRoom.getMember1();
//        ChattingLog chattingLog = ChattingLog.builder().chattingRoomId(chattingRoom.getId())
//                .sendMember(requestMember.getId())
//                .receiveMember(receiver)
//                .message("adadsdd")
//                .build();
//        chattingService.sendMessage(chattingLog); // 채팅 전송, 채팅이 암호화된 상태
//        Assertions.assertThat(chattingLog.getMessage()).isNotEqualTo("adadsdd");
//        List<ChattingLog> beforeSendChatting = new ArrayList<>();
//        beforeSendChatting.add(chattingLog);
//        secretTool.decryptChatting(beforeSendChatting); // 전송한 메시지를 다시 복호화
//        chattingLog = beforeSendChatting.get(0);
//        List<ChattingLog> chattingLogs = chattingService.getMonthlyChattingLogs(requestMember,chattingRoom, Timestamp.valueOf(LocalDateTime.now().minusHours(1)));
//        int count =0;
//        for (ChattingLog chat : chattingLogs){
//            if(chat.getMessage().contentEquals(chattingLog.getMessage()) && chat.getSendMember() == chattingLog.getSendMember() && chat.getReceiveMember() == chattingLog.getReceiveMember()){
//                count++;
//            }
//        }
//        Assertions.assertThat(count).isEqualTo(1);
//    }
//    @Test
//    @Order(3)
//    public void 채팅방나가기테스트() throws JsonProcessingException {
//        Member requestMember = memberRepository.selectMemberById(1); // 회원정보 조회
//        ChattingRoom chattingRoom = chattingService.getChattingRoom(requestMember); // 채팅방 조회
//        requestMember.setChattingRoomId(chattingRoom.getId());
//        chattingService.exitChattingRoom(requestMember); // 채팅방 나가기
//        ChattingRoom exitedChattingRoom = chattingService.getChattingRoom(requestMember,chattingRoom.getId()); // 나간 채팅방 조회
//        Member exitedMember = memberRepository.selectMemberById(1);
//        Assertions.assertThat(exitedMember.getChattingRoomId()).isNull();
//        Assertions.assertThat(exitedChattingRoom.getDeleteTime()).isNotNull();
//        Assertions.assertThat(exitedChattingRoom.getMember1()).isEqualTo(1);
//    }
//    @Test
//    @Order(4)
//    public void 채팅방랜덤생성테스트(){
//        Member requestMember = memberRepository.selectMemberById(1);
//        BigInteger chattingRoomId = chattingService.randomMatching(requestMember);
//        ChattingRoom chattingRoom = chattingService.getChattingRoom(requestMember);
//        Assertions.assertThat(chattingRoomId).isEqualTo(chattingRoom.getId());
//        Assertions.assertThat(requestMember.getId()).isEqualTo(chattingRoom.getMember1());
//        Assertions.assertThat(chattingRoom.getChattingTime()).isBetween(requestMember.getChattingTime(), requestMember.getChattingTime()+2);
//        Member member = memberRepository.selectMemberById(chattingRoom.getMember2());
//        Assertions.assertThat(member.getChattingTime()).isEqualTo(chattingRoom.getChattingTime());
//        if(requestMember.getMatchingSex()==Member.MatchingSex.ALL){ // 내 매칭성별이 "모두" 일 경우
//            if(member.getMatchingSex()==Member.MatchingSex.ALL) { // 상대방의 매칭성별이 "모두"일 경우
//            }else{ // 상대방의 매칭성별이 "모두"가 아닌 경우
//                Assertions.assertThat(member.getMatchingSex()).isEqualTo(requestMember.getMemberSex());
//            }
//        }else {
//            Assertions.assertThat(requestMember.getMatchingSex()).isEqualTo(member.getMemberSex()); // 요청자의 매칭성별과 매칭된 상대방의 성별 비교
//            if(member.getMatchingSex()==Member.MatchingSex.ALL) { // 상대방의 매칭성별이 "모두"일 경우
//            }else{ // 상대방의 매칭성별이 "모두"가 아닌 경우
//                Assertions.assertThat(member.getMatchingSex()).isEqualTo(requestMember.getMemberSex());
//            }
//        }
//        Assertions.assertThat(requestMember.getMajor()).isEqualTo(member.getMajor());
}
