package com.oclock.oclock.utils;

import com.oclock.oclock.dto.response.ErrorMessage;
import com.oclock.oclock.exception.OClockException;
import com.oclock.oclock.security.Jwt;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class TokenToID {
    public static long getIdFromToken(String tokenString, Jwt jwt) {
        try {
            Jwt.Claims claims = verify(tokenString, jwt);
            log.info(claims.toString());
            String claimString = claims.toString();
            claimString = claimString.substring(claimString.indexOf("["));
            String[] claimArr = claimString.split(",");
            return Integer.parseInt(claimArr[0].split("=")[1]);
        }catch (Exception e){
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .message("토큰이 유효하지 않습니다.").code(401).build();
            throw new OClockException(errorMessage);
        }
    }
    private static Jwt.Claims verify(String token, Jwt jwt) {
        return jwt.verify(token);
    }
}
