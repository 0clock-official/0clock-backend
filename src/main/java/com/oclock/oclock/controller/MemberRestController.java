package com.oclock.oclock.controller;

import com.oclock.oclock.dto.ApiResult;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.security.Jwt;
import com.oclock.oclock.security.JwtAuthentication;
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

    @GetMapping(path  = "getMembers")
    public ResponseEntity<ResponseDto> getMembers() {
        List<Member> members = memberService.getMembers();
        ResponseDto<String> response = ResponseDto.<String>builder()
                .code("200")
                .response("멤버 불러오기")
                .data(members.stream().map(Object::toString).collect(Collectors.joining(","))).build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "join/{email}/state")
    @ApiOperation(value = "회원가입 단계 확인")
    public ResponseEntity<ResponseDto> checkJoinStep(@PathVariable String email) {
        int step = memberService.checkJoinStep(new Email(email));
        ResponseDto<Integer> response = ResponseDto.<Integer>builder()
                .code("200")
                .response("회원가입 단계 확인")
                .data(step)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "join/step1")
    @ApiOperation(value = "회원가입절차 시작 - 이메일 인증")
    public ResponseEntity<ResponseDto> checkEmail(@RequestBody @ApiParam(value = "example: {\"email\": \"test@test.com\"}") Map<String, String> request
    ) {
        Email email = new Email(request.get("email"));
        ResponseDto<Email> response = ResponseDto.<Email>builder()
                .code("200")
                .response("이메일 인증")
                .data(email)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "join/step2")
    @ApiOperation(value = "회원가입 - 인증코드 인증")
    public ResponseEntity<ResponseDto> checkValidation(@AuthenticationPrincipal JwtAuthentication authentication, @RequestBody @ApiParam(value = "example:{ \"email\":\"test@test.com\",\n" + "\"code\":인증번호(숫자)\n" +
            "}") Map<String, String> request) {
        Email email = new Email(request.get("email"));
        String code = request.get("code");
        ResponseDto<Email> response = ResponseDto.<Email>builder()
                .code("200")
                .response("인증 코드 인증")
                .data(email)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "join/step3")
    @ApiOperation(value = "회원가입 - 비밀번호 입력")
    public ResponseEntity<ResponseDto> savePassword(@AuthenticationPrincipal JwtAuthentication authentication, @RequestBody @ApiParam(value = "example:{\n" +
            "\"token\":\"토큰(POST /members/join/step2 로 받은 토큰)\",\n" +
            "\"password\":\"비밀번호\"\n" +
            "}") Map<String, String> request) {
        String token = request.get("token");
        String password = request.get("password");
        ResponseDto<Boolean> response = ResponseDto.<Boolean>builder()
                .code("200")
                .response("비밀번호 입력")
                .data(true)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "join/step4")
    @ApiOperation(value = "학생증 인증을 위한 학생증 전송")
    public ResponseEntity<ResponseDto> saveIdCard(@AuthenticationPrincipal JwtAuthentication authentication, @RequestBody File file) {

        ResponseDto<Boolean> response = ResponseDto.<Boolean>builder()
                .code("200")
                .response("학생증 등록")
                .data(true)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "join/step5")
    @ApiOperation(value = "닉네임, 전공, 채팅시간, 성별 입력")
    public ResponseEntity<ResponseDto> savePassword(@AuthenticationPrincipal JwtAuthentication authentication) {
        ResponseDto<Boolean> response = ResponseDto.<Boolean>builder()
                .code("200")
                .response("개인 정보 변경")
                .data(true)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(path = "members")
    @ApiOperation(value = "자기 정보 불러오기")
    public ApiResult<Member> me(@AuthenticationPrincipal JwtAuthentication authentication) {
        return OK(memberService.findById(authentication.id));
    }

    @DeleteMapping(path = "members")
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
