package com.oclock.oclock.controller.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.oclock.oclock.dto.ChattingLog;
import com.oclock.oclock.dto.ChattingRoom;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.response.ErrorMessage;
import com.oclock.oclock.exception.ChattingException;
import com.oclock.oclock.rowmapper.MemberRowMapper;
import com.oclock.oclock.rowmapper.MemberRowMapperNoEmailAndChattingRoom;
import com.oclock.oclock.security.Jwt;
import com.oclock.oclock.service.ChattingService;
import com.oclock.oclock.service.MemberService;
import com.oclock.oclock.service.PushService;
import com.oclock.oclock.utils.TokenToID;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChattingHandler extends TextWebSocketHandler {
    @Autowired
    private ChattingService chattingService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PushService pushService;

    @Autowired
    private Jwt jwt;

    private static final Map<Long,WebSocketSession> memberIdSessionMap = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String text = message.getPayload();
        Map<String,String> textMap = mapper.readValue(text, new TypeReference<>() {});
        String accessToken = textMap.get("Authorization");
        String chattingMessage = textMap.get("message");
        // 엑세스 토큰 파싱 과정
        long memberId;
        try {
            memberId = new TokenToID().getIdFromAccessToken(accessToken, jwt);
        }catch (Exception e){
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .message("토큰이 유효하지 않습니다.")
                    .code(401).build();
            throw new ChattingException(errorMessage);
        }

        // 세션 맵에 추가
        memberIdSessionMap.put(memberId,session);

        // 채팅 dto 생성
        Member requestMember = memberService.findById(memberId,new MemberRowMapper<>());
        ChattingRoom chattingRoom = chattingService.getChattingRoom(requestMember);
        long receiveMemberId = chattingRoom.getMember1() == memberId? chattingRoom.getMember2():chattingRoom.getMember1();
        ChattingLog chattingLog = ChattingLog.builder()
                .message(chattingMessage)
                .chattingRoomId(requestMember.getChattingRoomId())
                .sendMember(memberId)
                .receiveMember(receiveMemberId)
                .build();

        // 채팅 전송
        chattingService.sendMessage(chattingLog);
        if(memberIdSessionMap.containsKey(receiveMemberId) && session.isOpen()){ // 상대방이 접속중인 경우
            Map<String,String> payloadMap = new HashMap<>();
            payloadMap.put("message",chattingMessage);
            payloadMap.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            TextMessage textMessage = new TextMessage(mapper.writeValueAsString(payloadMap));
            memberIdSessionMap.get(receiveMemberId).sendMessage(textMessage);
        }else{ // 상대가 접속중이 아닌 경우 = 푸쉬 메시지 보내야 함.
            Member receiver = memberService.findById(receiveMemberId,new MemberRowMapperNoEmailAndChattingRoom<>());
            Notification notification = Notification.builder()
                    .setTitle(receiver.getNickName())
                    .setBody(chattingMessage).build();
            Message pushMessage = Message.builder()
                    .setNotification(notification)
                    .setToken(receiver.getFcmToken()).build();
            pushService.pushMessage(pushMessage);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }
}
