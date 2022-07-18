package com.oclock.oclock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.ChattingTime;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.repository.ChattingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
class OclockBackendApplicationTests {
	private Member member1 = Member.builder()
			.id(1)
			.email("test@test.com")
			.chattingRoomId(BigInteger.valueOf(1))
			.nickName("dasdasda")
			.chattingTime(ChattingTime.TEN)
			.memberSex(Member.MemberSex.MALE)
			.matchingSex(Member.MatchingSex.ALL)
			.build();
	@Test
	void contextLoads() {
	}
	@Autowired
	private ChattingRepository chattingRepository;

	@Test
	public void addChatTest(){
		ChattingLog chattingLog = ChattingLog.builder().sendMember(1).receiveMember(2).chattingRoomId(BigInteger.ONE).message("dasdasdasd").build();
		chattingRepository.addChatting(chattingLog);
	}
	@Test
	public void addChattingRoomTest(){
		ChattingRoom chattingRoom = ChattingRoom.builder().chattingTime(1).member1(1).member2(2).build();
		chattingRepository.createChattingRoom(chattingRoom);
	}

	@Test
	public void getChattingListTest() throws JsonProcessingException {
		ChattingRoom chattingRoom = ChattingRoom.builder()
				.chattingTime(member1.getChattingTime())
				.member1(1)
				.member2(2)
				.id(BigInteger.ONE)
				.build();
		Timestamp start = Timestamp.valueOf(LocalDateTime.now().minusDays(3));
		Timestamp end = Timestamp.valueOf(start.toLocalDateTime().plusMonths(1));
		List<ChattingLog> before = chattingRepository.selectChattingLogs(member1,chattingRoom,start,end);
//		for (ChattingLog chat:before){
//			System.out.println(new ObjectMapper().writeValueAsString(chat));
//		}
//		System.out.println();
		addChatTest();
		List<ChattingLog> after = chattingRepository.selectChattingLogs(member1,chattingRoom,start,end);
//		for (ChattingLog chat:after){
//			System.out.println(new ObjectMapper().writeValueAsString(chat));
//		}
		Assertions.assertNotEquals(before,after);
	}

	@Test
	public void exitChattingRoomTest(){
		chattingRepository.exitChattingRoom(member1);
		Assertions.assertThrows(Exception.class,()->addChatTest());
	}

	@Test
	public void chattingMemberTest(){

	}
}
