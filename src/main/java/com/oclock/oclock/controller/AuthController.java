package com.oclock.oclock.controller;

import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.oclock.oclock.dto.Member;
import com.oclock.oclock.dto.RefreshRequest;
import com.oclock.oclock.model.Role;
import com.oclock.oclock.repository.MemberRepository;
import com.oclock.oclock.rowmapper.MemberRowMapper;
import com.oclock.oclock.security.AuthenticationResult;
import com.oclock.oclock.security.Jwt;
import com.oclock.oclock.security.JwtAuthenticationProvider;
import com.oclock.oclock.service.MemberService;
import com.oclock.oclock.service.TokenService;
import com.oclock.oclock.utils.TokenToID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  TokenService tokenService;
  @Autowired
  Jwt jwt;

  @Autowired
  MemberService memberService;

  @PostMapping("/accessToken")
  public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest refreshToken) {
    String accessToken = null;

    if (tokenService.isValidRefreshToken(refreshToken)) {
      Long memberId = TokenToID.getIdFromToken(refreshToken.getRefreshToken(), jwt);
      Member member =memberService.findById(memberId,new MemberRowMapper<>());
      accessToken = member.newApiToken(jwt, new String[]{Role.USER.value()});
    }
    else {
      ResponseDto<?> response = ResponseDto.<AuthenticationResult>builder()
          .code("401")
          .response("맞지 않는 재발급 토큰입니다.")
          .build();
      return new ResponseEntity<ResponseDto<?>>(response, HttpStatus.UNAUTHORIZED);

    }

    ResponseDto<?> response = ResponseDto.<AuthenticationResult>builder()
        .code("200")
        .response("재발급되었습니다.")
        .data(new AuthenticationResult(accessToken, refreshToken.getRefreshToken()))
        .build();
    return ResponseEntity.ok(response);
  }
}
