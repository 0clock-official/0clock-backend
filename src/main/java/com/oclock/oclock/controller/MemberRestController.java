package com.oclock.oclock.controller;

import com.oclock.oclock.dto.*;
import com.oclock.oclock.dto.response.OtherInfoDto;
import com.oclock.oclock.dto.response.SelfInfoDto;
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
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Value("${uploadPath}")
    private String uploadPath;
    @Autowired
    public MemberRestController(AuthenticationManager authenticationManager, Jwt jwt, JwtAuthenticationProvider jwtProvider, MemberService memberService, EmailService emailService, ChattingService chattingService) {
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
        this.jwtProvider = jwtProvider;
        this.memberService = memberService;
        this.emailService = emailService;
        this.chattingService = chattingService;
    }
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

    @PostMapping(value = "join/studentCard", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateEmailStudentCard(@AuthenticationPrincipal JwtAuthentication authentication, @RequestBody StudentCardDto studentCardDto) throws IOException {
        if (authentication == null) {
            ResponseDto<?> response = ResponseDto.<String >builder()
                .code("403")
                .response("인증 토큰이 없습니다.")
                .data("")
                .build();
            return ResponseEntity.status(403).body(response);
        }
        Member member = null;
        try {
            member = memberService.findByEmail(new Email(studentCardDto.getEmail()));
        } catch (Exception e) {
            e.getMessage();
            ResponseDto<?> response = ResponseDto.<String >builder()
                .code("400")
                .response("이메일이 없거나 잘못된 형식 입니다.")
                .data("")
                .build();
            return ResponseEntity.status(400).body(response);
        }
        String uploadFolder = uploadPath;
        File uploadPath = new File(uploadFolder);
        if(uploadPath.exists() == false) {
            uploadPath.mkdirs();
        }
        uploadPath.createNewFile();
        LoggerFactory.getLogger(this.getClass()).info(studentCardDto.toString());
        String img = studentCardDto.getIdCard();
        String[] imgAndExp = img.split(",");
        img = imgAndExp[1];
        String fileType = imgAndExp[0].split("/")[1].split(";")[0];
        byte[] decodedBytes = Base64.getDecoder().decode(img);
        FileUtils.writeByteArrayToFile(new File(uploadFolder +File.separator+member.getId()+"."+fileType), decodedBytes);

        ResponseDto<?> response = ResponseDto.<String >builder()
                .code("200")
                .response("학생증 업로드 성공")
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
    @ApiOperation(value = "자기 정보 불러오기") // 확인
    public ResponseEntity<ResponseDto> me(@AuthenticationPrincipal  JwtAuthentication authentication) {
        Member member = memberService.findById(authentication.id, (rs, rowNum) -> Member.builder()
                .email(new Email(rs.getString("email")))
                .nickName(rs.getString("nickName"))
                .major(rs.getInt("major"))
                .chattingTime(rs.getInt("chattingTime"))
                .memberSex(rs.getInt("memberSex"))
                .matchingSex(rs.getInt("matchingSex"))
                .build());
        SelfInfoDto selfInfoDto = new SelfInfoDto(member);
        ResponseDto<SelfInfoDto> dto = ResponseDto.<SelfInfoDto>builder()
                .code("200")
                .response("개인정보 조회에 성공하였습니다.")
                .data(selfInfoDto)
                .build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping(path = "other") // 확인
    @ApiOperation(value = "채팅하고있는 상대방 정보 조회")
    public ResponseEntity<?> other(@AuthenticationPrincipal JwtAuthentication authentication) {
        Member requestMember = memberService.findById(authentication.id,new MemberRowMapper<>());
        Member other = null;
        try {
            other = chattingService.getChattingMember(requestMember);
        } catch (IndexOutOfBoundsException e) {
            e.getMessage();
            ResponseDto<String> response = ResponseDto.<String>builder()
                .code("409")
                .response("채팅 상대방 정보 조회에 실패하였습니다.")
                .data("")
                .build();
            return ResponseEntity.status(409).body(response);
        }
        OtherInfoDto otherInfoDto = new OtherInfoDto(other);
        ResponseDto<OtherInfoDto> response = ResponseDto.<OtherInfoDto>builder()
                .code("200")
                .response("채팅 상대방 정보 조회에 성공하였습니다.")
                .data(otherInfoDto)
                .build();
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    @ApiOperation(value = "회원탈퇴")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal JwtAuthentication authentication) {
        memberService.deleteAccount(authentication.id);
        ResponseDto<String> response = ResponseDto.<String>builder()
                .code("200")
                .response("회원탈퇴 성공")
                .data("")
                .build();
        return ResponseEntity.ok().body(response);
    }
}
