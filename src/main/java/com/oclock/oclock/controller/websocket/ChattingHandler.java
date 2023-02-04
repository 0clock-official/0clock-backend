package com.oclock.oclock.controller.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChattingHandler extends TextWebSocketHandler {
    private enum ChattingType{
        MESSAGE,MESSAGE_OK, TIME_CHANGE_REQ, TIME_CHANGE_ACCEPT,TIME_CHANGE_REJECT, EXIT_CHATTINGROOM;
    }
    private final ChattingService chattingService;
    private final MemberService memberService;
    private final PushService pushService;
    private final Jwt jwt;

    public ChattingHandler(ChattingService chattingService, MemberService memberService, PushService pushService, Jwt jwt) {
        this.chattingService = chattingService;
        this.memberService = memberService;
        this.pushService = pushService;
        this.jwt = jwt;
    }

    private static final Map<Long,WebSocketSession> memberIdSessionMap = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChattingType chattingType = getChattingType(message);
        Map<String,String> paseMap = parsingPayLoad(message);
        long memberId = getIdFromAccessToken(paseMap.get("Authorization"));
        memberIdSessionMap.put(memberId,session);
        switch (chattingType){
            case MESSAGE:
                sendMessage(session,paseMap);
                break;
            case TIME_CHANGE_REQ:
                sendTimeChangeReq(session,paseMap);
                break;
            case TIME_CHANGE_ACCEPT:
                sendTimeChangeAccept(session,paseMap);
                break;
            case EXIT_CHATTINGROOM:
                exitChattingRoom(session,paseMap);
                break;
            case TIME_CHANGE_REJECT:
                sendTimeChangeReject(session,paseMap);
                break;
            default:
                ErrorMessage errorMessage = ErrorMessage.builder()
                        .message("지원하지 않는 타입입니다.")
                        .code(400).build();
                throw new ChattingException(errorMessage);
        }
    }
    private ChattingType getChattingType(TextMessage message) throws JsonProcessingException {
        Map<String ,String> parseMap = parsingPayLoad(message);
        return ChattingType.valueOf(parseMap.get("type"));
    }
    private Map<String ,String > parsingPayLoad(TextMessage message) throws JsonProcessingException {
        String text = message.getPayload();
        return mapper.readValue(text, new TypeReference<>() {});
    }

    private void sendMessage(WebSocketSession session,Map<String ,String> parseMap) throws IOException {
        String accessToken = parseMap.get("Authorization");
        String chattingMessage = parseMap.get("message");
        long memberId = getIdFromAccessToken(accessToken);

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
        chattingService.sendMessage(chattingLog, LocalTime.of(22,30));
        if(memberIdSessionMap.containsKey(receiveMemberId) && session.isOpen()){ // 상대방이 접속중인 경우
            Map<String,String> payloadMap = makeMessageMap(chattingMessage,ChattingType.MESSAGE);
            TextMessage textMessage = new TextMessage(mapper.writeValueAsString(payloadMap));
            sendSocketMessage(textMessage,receiveMemberId);
            Map<String,String > senderResponseMap = makeMessageMap("메세지 전송에 성공했습니다.",ChattingType.MESSAGE_OK);
            TextMessage senderResponseMessage = new TextMessage(mapper.writeValueAsString(senderResponseMap));
            session.sendMessage(senderResponseMessage);
        }else{ // 상대가 접속중이 아닌 경우 = 푸쉬 메시지 보내야 함.
            sendPushMessage(chattingMessage,receiveMemberId);
        }
    }
    private long getIdFromAccessToken(String accessToken){
        long memberId;
        try {
            memberId = TokenToID.getIdFromToken(accessToken, jwt);
        }catch (Exception e){
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .message("토큰이 유효하지 않습니다.")
                    .code(401).build();
            throw new ChattingException(errorMessage);
        }
        return memberId;
    }
    private Map<String,String> makeMessageMap(String chattingMessage,ChattingType type){
        Map<String,String> payloadMap = new HashMap<>();
        payloadMap.put("message",chattingMessage);
        payloadMap.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        payloadMap.put("type",type.name());
        return payloadMap;
    }
    private void sendSocketMessage(TextMessage message,long receiveMemberId) throws IOException {
        memberIdSessionMap.get(receiveMemberId).sendMessage(message);
    }
    private void sendPushMessage(String chattingMessage,long receiverId){
        Member receiver = memberService.findById(receiverId,new MemberRowMapperNoEmailAndChattingRoom<>());
        Notification notification = Notification.builder()
                .setTitle(receiver.getNickName())
                .setBody(chattingMessage).build();
        Message pushMessage = Message.builder()
                .setNotification(notification)
                .setToken(receiver.getFcmToken()).build();
        pushService.pushMessage(pushMessage);
    }
    private void sendTimeChangeReq(WebSocketSession session,Map<String ,String> parseMap) throws Exception {
        String accessToken = parseMap.get("Authorization");
        int chattingTime = Integer.parseInt(parseMap.get("message"));
        long memberId = getIdFromAccessToken(accessToken);
        Member member = memberService.findById(memberId,new MemberRowMapper<>());
        chattingService.requestChangeChattingTime(member,chattingTime);
        Map<String,String> senderResponseMap = makeMessageMap("채팅 시간 변경 요청에 성공했습니다.",ChattingType.TIME_CHANGE_REQ);
        TextMessage senderResponseMessage = new TextMessage(mapper.writeValueAsString(senderResponseMap));
        session.sendMessage(senderResponseMessage);

        ChattingRoom chattingRoom = chattingService.getChattingRoom(member);
        long receiveMemberId = chattingRoom.getMember1() == memberId? chattingRoom.getMember2():chattingRoom.getMember1();
        String chattingMessage = "채팅 시간 변경요청이 들어왔습니다 : {?}".replace("?",chattingTime+"");
        if(memberIdSessionMap.containsKey(receiveMemberId) && session.isOpen()){ // 상대방이 접속중인 경우
            Map<String,String> payloadMap = makeMessageMap(chattingMessage,ChattingType.TIME_CHANGE_REQ);
            TextMessage textMessage = new TextMessage(mapper.writeValueAsString(payloadMap));
            sendSocketMessage(textMessage,receiveMemberId);
        }else{ // 상대가 접속중이 아닌 경우 = 푸쉬 메시지 보내야 함.
            sendPushMessage(chattingMessage,receiveMemberId);
        }
    }
    private void sendTimeChangeAccept(WebSocketSession session,Map<String ,String> parseMap) throws Exception{
        String accessToken = parseMap.get("Authorization");
        long memberId = getIdFromAccessToken(accessToken);
        Member member = memberService.findById(memberId,new MemberRowMapper<>());
        Map<String,String> senderResponseMap = makeMessageMap("채팅 시간 변경을 수락했습니다.",ChattingType.TIME_CHANGE_ACCEPT);
        TextMessage senderResponseMessage = new TextMessage(mapper.writeValueAsString(senderResponseMap));
        session.sendMessage(senderResponseMessage);

        ChattingRoom chattingRoom = chattingService.getChattingRoom(member);
        int chattingTime = chattingService.getRequestChangeChattingTime(chattingRoom);
        chattingService.acceptChangeChattingTime(member,chattingTime);
        long receiveMemberId = chattingRoom.getMember1() == memberId? chattingRoom.getMember2():chattingRoom.getMember1();
        String chattingMessage = "채팅시간 변경이 수락되었습니다 {?}".replace("?",chattingTime+"");
        if(memberIdSessionMap.containsKey(receiveMemberId) && session.isOpen()){ // 상대방이 접속중인 경우
            Map<String,String> payloadMap = makeMessageMap(chattingMessage,ChattingType.TIME_CHANGE_ACCEPT);
            TextMessage textMessage = new TextMessage(mapper.writeValueAsString(payloadMap));
            sendSocketMessage(textMessage,receiveMemberId);
        }else{ // 상대가 접속중이 아닌 경우 = 푸쉬 메시지 보내야 함.
            sendPushMessage(chattingMessage,receiveMemberId);
        }
    }
    private void exitChattingRoom(WebSocketSession session,Map<String ,String> parseMap) throws Exception {
        String accessToken = parseMap.get("Authorization");
        long memberId = getIdFromAccessToken(accessToken);
        Member member = memberService.findById(memberId, new MemberRowMapper<>());
        ChattingRoom chattingRoom = chattingService.getChattingRoom(member);
        long receiveMemberId = chattingRoom.getMember1() == memberId? chattingRoom.getMember2():chattingRoom.getMember1();
        chattingService.exitChattingRoom(member);
        String chattingMessage = "상대방이 채팅방을 나갔습니다.";
        if(memberIdSessionMap.containsKey(receiveMemberId) && session.isOpen()){ // 상대방이 접속중인 경우
            Map<String,String> payloadMap = makeMessageMap(chattingMessage,ChattingType.EXIT_CHATTINGROOM);
            TextMessage textMessage = new TextMessage(mapper.writeValueAsString(payloadMap));
            sendSocketMessage(textMessage,receiveMemberId);
        }else{ // 상대가 접속중이 아닌 경우 = 푸쉬 메시지 보내야 함.
            sendPushMessage(chattingMessage,receiveMemberId);
        }
        session.close();
    }
    private void sendTimeChangeReject(WebSocketSession session,Map<String ,String> parseMap) throws Exception{
        String accessToken = parseMap.get("Authorization");
        long memberId = getIdFromAccessToken(accessToken);
        Member member = memberService.findById(memberId,new MemberRowMapper<>());
        Map<String,String> senderResponseMap = makeMessageMap("채팅 시간 변경을 거절했습니다.",ChattingType.TIME_CHANGE_REJECT);
        TextMessage senderResponseMessage = new TextMessage(mapper.writeValueAsString(senderResponseMap));
        session.sendMessage(senderResponseMessage);

        ChattingRoom chattingRoom = chattingService.getChattingRoom(member);
        long receiveMemberId = chattingRoom.getMember1() == memberId? chattingRoom.getMember2():chattingRoom.getMember1();
        String chattingMessage = "채팅시간 변경이 거절되었습니다.";
        if(memberIdSessionMap.containsKey(receiveMemberId) && session.isOpen()){ // 상대방이 접속중인 경우
            Map<String,String> payloadMap = makeMessageMap(chattingMessage,ChattingType.TIME_CHANGE_REJECT);
            TextMessage textMessage = new TextMessage(mapper.writeValueAsString(payloadMap));
            sendSocketMessage(textMessage,receiveMemberId);
        }else{ // 상대가 접속중이 아닌 경우 = 푸쉬 메시지 보내야 함.
            sendPushMessage(chattingMessage,receiveMemberId);
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        for (long key:memberIdSessionMap.keySet()) {
            if(memberIdSessionMap.get(key).equals(session)){
                memberIdSessionMap.remove(session);
                break;
            }
        }
        super.afterConnectionClosed(session, status);
    }
}
