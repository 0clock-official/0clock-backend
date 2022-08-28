package com.oclock.oclock.config;

import com.oclock.oclock.security.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;

@RequiredArgsConstructor
public class WebSecurityConfigure extends WebSecurityConfigurer {
    private final Jwt jwt;

    private final JwtTokenConfigure jwtTokenConfigure;

    private final JwtAccessDeniedHandler accessDeniedHandler;

    private final EntryPointUnauthorizedHandler unauthorizedHandler;
}
