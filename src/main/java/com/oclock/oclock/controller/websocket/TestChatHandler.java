package com.oclock.oclock.controller.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Set;
@Component
public class TestChatHandler extends TextWebSocketHandler {
    private static Set<WebSocketSession> sessions = new HashSet<>();
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if(sessions.add(session)){
            System.out.println(session+" 접속");
        }else {
            System.out.println(session+" 중복");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if (sessions.contains(session)){
            String payload = message.getPayload();
            System.out.println(payload);
        }
        for (WebSocketSession s :
                sessions) {
            if(s==session)continue;
            TextMessage textMessage = new TextMessage(session+"이 "+message.getPayload()+"라고 보냈습니다.");
            s.sendMessage(textMessage);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if(sessions.remove(session)){
            System.out.println(session+" 종료");
        }else {
            System.out.println(session+" 이미 종료됨");
        }
    }
}
