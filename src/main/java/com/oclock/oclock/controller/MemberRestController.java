package com.oclock.oclock.controller;

import com.oclock.oclock.dto.ApiResult;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.MemberDto;
import com.oclock.oclock.exception.UnauthorizedException;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.rowmapper.MemberRowMapper;
import com.oclock.oclock.security.*;
import com.oclock.oclock.service.ChattingService;
import com.oclock.oclock.service.EmailService;
import com.oclock.oclock.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.mail.MessagingException;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private final ChattingService chattingService;

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

    @PostMapping(path = "email") // 확인
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> request) throws MessagingException, NoSuchAlgorithmException {
        String email = request.get("email");
        String verification = memberService.createRandomCode();
        memberService.renewVerification(email, verification);
        emailService.sendVerificationHtmlMessage(new Email(email), verification);
        ResponseDto<?> response = ResponseDto.<String>builder()
                .code("200 OK")
                .response("이메일로 인증코드가 전송되었습니다.")
                .data("")
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "email/verification") //확인
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


    @PutMapping(path = "fcm") //확인
    public ResponseEntity<ResponseDto> refreshFcm(@AuthenticationPrincipal  JwtAuthentication authentication, @RequestBody Map<String, String> body) {
        String fcm = body.get("fcm");
        memberService.updateFcm(authentication.id,fcm);
        return ResponseEntity.ok(ResponseDto.builder().response("fcm 토큰이 갱신되었습니다.").code("200").data("").build());
    }

    @PostMapping(path = "join/studentCard") // ToDo 구현 필요
    public ResponseEntity<?> updateEmailStudentCard(@RequestHeader Map<String, String> header, @RequestBody Map<String, String> body) {
        memberService.updateEmailStudentCard(body);
        ResponseDto<?> response = ResponseDto.<String >builder()
                .code("200")
                .response("학생증 인증 성공")
                .data("")
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(path = "login") // 확인
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
                    .response("로그인 되었습니다.")
                    .data(new AuthenticationResult(jwtProvider.getAccessToken(), jwtProvider.getRefreshToken()))
                    .build();
            return ResponseEntity.ok().body(response);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    @PostMapping(path = "passwordReset") // 미구현
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        Member member = memberService.findByEmail(new Email(email));
        //Todo 이메일로 비밀번호 초기화 링크가 전송되어야 함.
        ResponseDto<?> response = ResponseDto.<String>builder()
                .code("200")
                .response("비밀번호 초기화 링크가 메일로 전송되었습니다.")
                .data("")
                .build();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping // 확인
    public ResponseEntity<ResponseDto> editMyself(@AuthenticationPrincipal  JwtAuthentication authentication,@RequestBody Map<String, String> body) {
        memberService.editMyself(authentication.id,body);
        return ResponseEntity.ok().body(ResponseDto.builder().response("개인정보 수정이 완료되었습니다.").code("200").data("").build());
    }

    @GetMapping(path = "self")
    @ApiOperation(value = "자기 정보 불러오기") // 확인 Todo 응답에 불필요한 값 포함 안되게 수정 필요
    public ResponseEntity<ResponseDto> me(@AuthenticationPrincipal  JwtAuthentication authentication) {
        Member member = memberService.findById(authentication.id, (rs, rowNum) -> Member.builder()
                .email(new Email(rs.getString("email")))
                .nickName(rs.getString("nickName"))
                .major(rs.getInt("major"))
                .chattingTime(rs.getInt("chattingTime"))
                .memberSex(rs.getInt("memberSex"))
                .matchingSex(rs.getInt("matchingSex"))
                .build());
        ResponseDto<Member> dto = ResponseDto.<Member>builder()
                .code("200")
                .response("개인정보 조회에 성공하였습니다.")
                .data(member)
                .build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping(path = "other") // 확인 Todo 응답에 불필요한 값 포함 안되게 수정 필요
    @ApiOperation(value = "채팅하고있는 상대방 정보 조회")
    public ResponseEntity<?> other(@AuthenticationPrincipal JwtAuthentication authentication) {
        Member requestMember = memberService.findById(authentication.id,new MemberRowMapper<>());
        Member other = chattingService.getChattingMember(requestMember);
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
