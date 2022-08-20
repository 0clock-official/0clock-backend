package com.oclock.oclock.controller;

import com.oclock.oclock.dto.ApiResult;
import com.oclock.oclock.dto.JoinRequest;
import com.oclock.oclock.dto.JoinResult;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.model.Email;
import com.oclock.oclock.model.Role;
import com.oclock.oclock.security.Jwt;
import com.oclock.oclock.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.oclock.oclock.dto.ApiResult.OK;

@RestController
@RequestMapping("api")
@Api(tags = "사용자 APIs")
public class MemberRestController {

    private final Jwt jwt;

    private final MemberService memberService;

    public MemberRestController(Jwt jwt, MemberService memberService) {
        this.jwt = jwt;
        this.memberService = memberService;
    }

    @PostMapping(path = "member/exists")
    @ApiOperation(value = "이메일 중복확인")
    public ApiResult<Boolean> checkEmail(@RequestBody @ApiParam(value = "example: {\"address\": \"test00@gmail.com\"}") Map<String, String> request
    ) {
        Email email = new Email(request.get("address"));
        return OK(memberService.checkEmail(email));
    }

    @PostMapping(path = "member/join")
    @ApiOperation(value = "회원가입")
    public ApiResult<JoinResult> join(@RequestBody JoinRequest joinRequest) {
        Member member = memberService.join(new Email(joinRequest.getPrincipal()), joinRequest.getCredentials());
        String apiToken = member.newApiToken(jwt, new String[]{Role.USER.value()});
        return OK(
                new JoinResult(apiToken, member)
        );
    }
}
