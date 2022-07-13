package com.oclock.oclock.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class OClockException extends RuntimeException{

    @Getter
    @NoArgsConstructor
    private class OClockError{
        private Timestamp timestamp;
        private int status;
        private String error;
        private String path;
    }

    public ResponseEntity<?> toResponseEntity(HttpServletRequest request, HttpServletResponse response){
        OClockError oClockError = new OClockError();
        oClockError.timestamp = Timestamp.valueOf(LocalDateTime.now());
        oClockError.status = response.getStatus();
        oClockError.error = getMessage();
        oClockError.path = request.getMethod() + ": "+ request.getRequestURI()+"?"+request.getQueryString();
        return ResponseEntity.status(oClockError.status).body(oClockError);
    }
}
