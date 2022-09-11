package com.oclock.oclock.controller;

import com.oclock.oclock.dto.ApiResult;
import com.oclock.oclock.exception.UnauthorizedException;
import com.oclock.oclock.security.AuthenticationRequest;
import com.oclock.oclock.security.AuthenticationResult;
import com.oclock.oclock.security.JwtAuthenticationToken;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.oclock.oclock.dto.ApiResult.OK;


@Slf4j
@RestController
@RequestMapping("api/auth")
@Api(tags = "인증 APIs")
public class AuthenticationRestController {

    private final AuthenticationManager authenticationManager;

    public AuthenticationRestController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping
    @ApiOperation(value = "사용자 로그인 (API 토큰 필요없음)")
    public ApiResult<AuthenticationResultDto> authentication(@RequestBody AuthenticationRequest authRequest) throws UnauthorizedException {
        try {
            JwtAuthenticationToken authToken = new JwtAuthenticationToken(authRequest.getPrincipal(), authRequest.getCredentials());
            log.info(authRequest.toString());
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return OK(
                    new AuthenticationResultDto((AuthenticationResult) authentication.getDetails())
            );
        } catch (AuthenticationException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

}