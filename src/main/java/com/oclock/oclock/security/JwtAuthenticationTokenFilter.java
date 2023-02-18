package com.oclock.oclock.security;

import com.oclock.oclock.dto.response.ErrorMessage;
import com.oclock.oclock.exception.OClockException;
import com.oclock.oclock.exception.UnauthorizedException;
import com.oclock.oclock.model.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

public class JwtAuthenticationTokenFilter extends GenericFilterBean {
    private static final Pattern BEARER = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String headerKey;

    private final Jwt jwt;

    public JwtAuthenticationTokenFilter(String headerKey, Jwt jwt) {
        this.headerKey = headerKey;
        this.jwt = jwt;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // SecurityContextHolder 에서 인증정보를 찾을 수 없다면...
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // HTTP 요청 Header 에서 JWT 값을 가져와본다.
            String authorizationToken = obtainAuthorizationToken(request);
            // JWT 값이 있다면, JWT 값을 검증하고 인증정보를 생성해 SecurityContextHolder 에 추가한다.
            if (authorizationToken != null) {
                try {
                    Jwt.Claims claims = verify(authorizationToken);
                    log.debug("Jwt parse result: {}", claims);

                    // 만료 10분 전
                    if (canRefresh(claims, 6000 * 10)) {
                        String refreshedToken = jwt.refreshToken(authorizationToken);
                        response.setHeader(headerKey, refreshedToken);
                    }

                    Long userKey = claims.userKey;
                    log.debug(userKey.toString());

                    List<GrantedAuthority> authorities = obtainAuthorities(claims);

                    if (nonNull(userKey) && authorities.size() > 0) {
                        log.debug(userKey.toString());
                        JwtAuthenticationToken authentication =
                                new JwtAuthenticationToken(new JwtAuthentication(userKey), null, authorities);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                } catch (Exception e) {
                    ErrorMessage errorMessage = ErrorMessage.builder()
                            .code(401)
                            .message("토큰이 만료되었습니다.").build();
                    throw new OClockException(errorMessage);
                }
            }
        } else {
            log.debug("SecurityContextHolder not populated with security token, as it already contained: '{}'",
                    SecurityContextHolder.getContext().getAuthentication());
        }

        chain.doFilter(request, response);
    }

    private boolean canRefresh(Jwt.Claims claims, long refreshRangeMillis) {
        long exp = claims.exp();
        if (exp > 0) {
            long remain = exp - System.currentTimeMillis();
            return remain < refreshRangeMillis;
        }
        return false;
    }

    private List<GrantedAuthority> obtainAuthorities(Jwt.Claims claims) {
        String[] roles = claims.roles;
        return roles == null || roles.length == 0 ?
                Collections.emptyList() :
                Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(toList());
    }

    private String obtainAuthorizationToken(HttpServletRequest request) {
        String token = request.getHeader(headerKey);
        if (token != null) {
            if (log.isDebugEnabled())
                log.debug("Jwt authorization api detected: {}", token);
            try {
                token = URLDecoder.decode(token, "UTF-8");
                String[] parts = token.split(" ");
                if (parts.length == 2) {
                    String scheme = parts[0];
                    String credentials = parts[1];
                    return BEARER.matcher(scheme).matches() ? credentials : null;
                }
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }

        return null;
    }

    private Jwt.Claims verify(String token) {
        return jwt.verify(token);
    }
}
