package com.oclock.oclock.utils;

import com.oclock.oclock.security.Jwt;
import com.oclock.oclock.security.JwtAuthentication;
import com.oclock.oclock.security.JwtAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import static java.util.Objects.nonNull;
@Slf4j
public class TokenToID {
    public long getIdFromAccessToken(String tokenString,Jwt jwt) {
        if (tokenString != null) {
            try {
                Jwt.Claims claims = verify(tokenString,jwt);
                log.info(claims.toString());
                String claimString = claims.toString();
                claimString = claimString.substring(claimString.indexOf("["));
                String[] claimArr = claimString.split(",");
                return Integer.parseInt(claimArr[0].split("=")[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
    private Jwt.Claims verify(String token, Jwt jwt) {
        return jwt.verify(token);
    }
}
