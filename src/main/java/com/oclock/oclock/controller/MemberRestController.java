package com.oclock.oclock.controller;

import com.oclock.oclock.dto.ApiResult;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.MemberDto;
import com.oclock.oclock.exception.UnauthorizedException;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.security.*;
import com.oclock.oclock.service.EmailService;
import com.oclock.oclock.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.mail.MessagingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.oclock.oclock.dto.ApiResult.OK;

@Slf4j
@RestController
@RequestMapping("members")
@Api(tags = "사용자 APIs")
@AllArgsConstructor
public class MemberRestController {

    private final AuthenticationManager authenticationManager;

    private final Jwt jwt;

    private final JwtAuthenticationProvider jwtProvider;

    private final MemberService memberService;

    private final EmailService emailService;

    //Test용 Get멤버. Member의 객체 정보만 반환해야함

    @GetMapping(path  = "getMembers")
    public ResponseEntity<ResponseDto> getMembers() {
        List<Member> members = memberService.getMembers();
        ResponseDto<String> response = ResponseDto.<String>builder()
                .code("200 OK")
                .response("멤버 불러오기")
                .data(members.stream().map(Object::toString).collect(Collectors.joining(","))).build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "email")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> request) throws MessagingException, NoSuchAlgorithmException {
        String email = request.get("email");
        String verification = memberService.createRandomCode();
        memberService.renewVerification(email, verification);
        emailService.sendVerificationHtmlMessage(new Email(email), verification);
        ResponseDto<?> response = ResponseDto.<String>builder()
                .code("200 OK")
                .response("")
                .data("")
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "email/verification")
    public ResponseEntity<?> checkVerification(@RequestBody Map<String, String> request) throws MessagingException {
        String email = request.get("email");
        if (memberService.checkVerification(email, request.get("code"))) {
            ResponseDto<?> response = ResponseDto.<String>builder()
                    .code("200 OK")
                    .response("")
                    .data("")
                    .build();
            return ResponseEntity.ok().body(response);
        }
        ResponseDto<?> response = ResponseDto.<String>builder()
                .code("401 Unauthorized")
                .response("wrong code")
                .data("")
                .build();
        return ResponseEntity.status(401).body(response);
    }


    @PostMapping(path = "join")
    public ResponseEntity<?> join(@RequestBody MemberDto memberDto) {
        memberService.join(memberDto);
        try {
            JwtAuthenticationToken authToken = new JwtAuthenticationToken(memberDto.getEmail(), memberDto.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Member member = memberService.findByEmail(new Email(memberDto.getEmail()));

            memberService.mergeToken(member.getId(), jwtProvider.getRefreshToken());

            ResponseDto<?> response = ResponseDto.<AuthenticationResult>builder()
                    .code("200 OK")
                    .response("")
                    .data(new AuthenticationResult(jwtProvider.getAccessToken(), jwtProvider.getRefreshToken()))
                    .build();
            return ResponseEntity.status(200).body(response);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }


    @PutMapping(path = "fcm")
    public void refreshFcm(@RequestBody Map<String, String> body) {
        memberService.updateFcm(body);
    }

    @PostMapping(path = "join/studentCard")
    public ResponseEntity<?> updateEmailStudentCard(@RequestHeader Map<String, String> header, @RequestBody Map<String, String> body) {
        memberService.updateEmailStudentCard(body);
        ResponseDto<?> response = ResponseDto.<String >builder()
                .code("200")
                .response("학생증 인증 성공")
                .data("")
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "login")
    @ApiOperation(value = "사용자 로그인 (API 토큰 필요없음)")
    public ResponseEntity<?> authentication(@RequestBody AuthenticationRequest authRequest) throws UnauthorizedException {
        try {
            JwtAuthenticationToken authToken = new JwtAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
            Authentication authentication = jwtProvider.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Member member = memberService.findByEmail(new Email(authRequest.getEmail()));

            memberService.mergeToken(member.getId(), jwtProvider.getRefreshToken());
            ResponseDto<?> response = ResponseDto.<AuthenticationResult>builder()
                    .code("200")
                    .response("")
                    .data(new AuthenticationResult(jwtProvider.getAccessToken(), jwtProvider.getRefreshToken()))
                    .build();
            return ResponseEntity.ok().body(response);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    @PostMapping(path = "{email}/passwordReset")
    public ResponseEntity<?> resetPassword(@PathVariable("email") String email, @AuthenticationPrincipal JwtAuthentication authentication, @RequestBody Map<String, String> body) throws Exception {
        Member member = memberService.findByEmail(new Email(email));
        if (member.getId() != authentication.id) throw new UnauthorizedException("Unauthorized access");
        member.setPassword(body.get("password"));
        if (member.getPassword() == null) throw new Exception("There is no password input");
        memberService.resetPassword(member);
        ResponseDto<?> response = ResponseDto.<String>builder()
                .code("200")
                .response("이메일 전송 성공")
                .data("")
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping
    public ResponseEntity<?> editMyself(@AuthenticationPrincipal JwtAuthentication authentication, @RequestBody Map<String, String> body) {
        memberService.editMyself(body , authentication.id);
        return ResponseEntity.ok().body("");
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
}
