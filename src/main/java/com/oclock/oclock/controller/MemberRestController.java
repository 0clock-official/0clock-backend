package com.oclock.oclock.controller;

import com.oclock.oclock.dto.ApiResult;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.exception.NotFoundException;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.security.Jwt;
import com.oclock.oclock.security.JwtAuthentication;
import com.oclock.oclock.service.EmailService;
import com.oclock.oclock.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.oclock.oclock.dto.ApiResult.OK;

@RestController
@RequestMapping("members")
@Api(tags = "사용자 APIs")
@AllArgsConstructor
public class MemberRestController {

    private final Jwt jwt;

    private final MemberService memberService;

    private final EmailService emailService;

    @GetMapping(path  = "getMembers")
    public ResponseEntity<ResponseDto> getMembers() {
        List<Member> members = memberService.getMembers();
        ResponseDto<String> response = ResponseDto.<String>builder()
                .code("200")
                .response("멤버 불러오기")
                .data(members.stream().map(Object::toString).collect(Collectors.joining(","))).build();
        return ResponseEntity.ok().body(response);
    }

    //TODO 랜덤 번호 서비스단에서 처리 리포에서 DB로 저장
    @PostMapping(path = "email")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> request) {
        emailService.sendSimpleMessage(request.get("to"), request.get("subject"), request.get("text"));
        ResponseDto<Boolean> response = ResponseDto.<Boolean>builder()
                .code("200")
                .response("이메일 전송 성공")
                .data(true)
                .build();
        return ResponseEntity.ok().body(response);
    }


    
    //TODO fcm 재발급 구현

    @PutMapping(path = "fcm")
    public ResponseEntity<?> refreshFcm() {
        
    }

    //TODO 업로드 구현
    @PostMapping(path = "join/studentCard")
    public ResponseEntity<?> updateEmailStudentCard(@RequestHeader Map<String, String> header, @RequestBody Map<String, String> body) {
        memberService.updateEmailStudnetCard(body);
    }
    
    //TODO login 여기에 구현


    @PostMapping(path = "{email}/passwordReset")
    public ResponseEntity<?> resetPassword(@RequestHeader Map<String, String> header, @RequestBody Map<String, String> body) {
        Member member = memberService.findByEmail(new Email(body.get("email")));
        member.setPassword(body.get("password"));
        ResponseDto<Boolean> response = ResponseDto.<Boolean>builder()
                .code("200")
                .response("이메일 전송 성공")
                .data(true)
                .build();
        return ResponseEntity.ok().body(response);

    }

    @PutMapping
    public ResponseEntity<?> editMyself(@RequestBody Map<String, String> body) {
        memberService.editMyself(body);
    }

    @GetMapping(path = "self")
    @ApiOperation(value = "자기 정보 불러오기")
    public ApiResult<Member> me(@AuthenticationPrincipal JwtAuthentication authentication) {
        return OK(memberService.findById(authentication.id));
    }

    @GetMapping(path = "other")
    @ApiOperation(value = "채팅하고있는 상대방 정보 조회")
    public ResponseEntity<?> other(@AuthenticationPrincipal JwtAuthentication authentication) {
        Member other = memberService.other(authentication.id);
        ResponseDto<Member> response = ResponseDto.<Member>builder()
                .code("200")
                .response("상대방 정보 불러오기 성공")
                .data(other)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    @ApiOperation(value = "회원탈퇴")
    public ApiResult<Boolean> deleteAccount(@AuthenticationPrincipal JwtAuthentication authentication) {
        return OK(memberService.deleteAccount(authentication.id));
    }




//    @PostMapping(path = "join")
//    @ApiOperation(value = "회원가입")
//    public ApiResult<JoinResult> join(@RequestBody JoinRequest joinRequest) {
//        Member member = memberService.join(new Email(joinRequest.getPrincipal()), joinRequest.getCredentials());
//        String apiToken = member.newApiToken(jwt, new String[]{Role.USER.value()});
//        return OK(
//                new JoinResult(apiToken, member)
//        );
//    }
}
