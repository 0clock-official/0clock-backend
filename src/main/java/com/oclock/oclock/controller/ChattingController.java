package com.oclock.oclock.controller;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.rowmapper.MemberRowMapper;
import com.oclock.oclock.security.JwtAuthentication;
import com.oclock.oclock.service.ChattingService;
import com.oclock.oclock.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("chatting")
public class ChattingController {
    @Autowired
    private ChattingService chattingService;
    @Autowired
    private MemberService memberService;

    @PostMapping("/room/rand")
    public ResponseEntity<ResponseDto<?>> createChatting(@AuthenticationPrincipal JwtAuthentication authentication){
        Member requestMember = memberService.findById(authentication.id, new MemberRowMapper<>());
        BigInteger chattingRoomId = chattingService.randomMatching(requestMember);
        Map<String,BigInteger> requestMap = new HashMap<>();
        requestMap.put("chattingRoomId",chattingRoomId);
        ResponseDto<Map<String,BigInteger>> responseDto = ResponseDto.<Map<String,BigInteger>>builder()
                .response("채팅방참여에 성공하였습니다.")
                .code("200")
                .data(requestMap).build();
        return ResponseEntity.ok(responseDto);
    }
}
