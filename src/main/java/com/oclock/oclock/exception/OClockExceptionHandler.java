package com.oclock.oclock.exception;

import com.oclock.oclock.dto.response.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OClockExceptionHandler {
    @ExceptionHandler(OClockException.class)
    public ResponseEntity<ErrorMessage> handle(OClockException e){
        return ResponseEntity.status(e.getErrorMessage().getCode()).body(e.getErrorMessage());
    }
}
