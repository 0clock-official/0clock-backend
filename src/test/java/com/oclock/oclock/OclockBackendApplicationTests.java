package com.oclock.oclock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.ChattingTime;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.repository.ChattingRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
//	@Test
//	void contextLoads() {
//	}
	@Autowired
	private ChattingRepository chattingRepository;

	@Test
	@Order(2)
	public void addChatTest(){
		ChattingLog chattingLog = ChattingLog.builder().sendMember(1).receiveMember(2).chattingRoomId(BigInteger.ONE).message("정상상태 메시지 송수신").build();
		chattingRepository.addChatting(chattingLog);
	}
	@Test
	@Order(1)
	public void addChattingRoomTest(){
		Assertions.assertNotEquals(chattingRepository,null);
		ChattingRoom chattingRoom = ChattingRoom.builder().chattingTime(1).member1(1).member2(2).build();
		chattingRepository.createChattingRoom(chattingRoom);
	}

	@Test
	@Order(3)
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
		ChattingLog chattingLog = ChattingLog.builder().sendMember(1).receiveMember(2).chattingRoomId(BigInteger.ONE).message("상태변경을 위한 메시지 송수신").build();
		chattingRepository.addChatting(chattingLog);

		List<ChattingLog> after = chattingRepository.selectChattingLogs(member1,chattingRoom,start,end);
//		for (ChattingLog chat:after){
//			System.out.println(new ObjectMapper().writeValueAsString(chat));
//		}
		Assertions.assertNotEquals(before.size(),after.size());
	}

	@Test
	@Order(4)
	public void exitChattingRoomTest(){
		ChattingRoom chattingRoom = ChattingRoom.builder()
				.chattingTime(member1.getChattingTime())
				.member1(1)
				.member2(2)
				.id(BigInteger.ONE)
				.build();
		chattingRepository.exitChattingRoom(member1);
		ChattingLog chattingLog = ChattingLog.builder().sendMember(1).receiveMember(2).chattingRoomId(BigInteger.ONE).message("채팅방 나간 후 메시지 송수신").build();
		chattingRepository.addChatting(chattingLog);
		Timestamp start = Timestamp.valueOf(LocalDateTime.now().minusDays(3));
		Timestamp end = Timestamp.valueOf(start.toLocalDateTime().plusMonths(1));
		List<ChattingLog> logs = chattingRepository.selectChattingLogs(member1,chattingRoom,start,end);
		for (ChattingLog chat: logs) {
			Assertions.assertNotEquals("채팅방 나간 후 메시지 송수신",chat.getMessage());
		}
	}

//	@Test
//	public void chattingMemberTest(){
//
//	}
}
