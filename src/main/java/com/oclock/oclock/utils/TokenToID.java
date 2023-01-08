package com.oclock.oclock.utils;

import com.oclock.oclock.security.Jwt;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenToID {
    public static long getIdFromToken(String tokenString, Jwt jwt) {
        Jwt.Claims claims = verify(tokenString,jwt);
        log.info(claims.toString());
        String claimString = claims.toString();
        claimString = claimString.substring(claimString.indexOf("["));
        String[] claimArr = claimString.split(",");
        return Integer.parseInt(claimArr[0].split("=")[1]);
    }
    private static Jwt.Claims verify(String token, Jwt jwt) {
        return jwt.verify(token);
    }
}
