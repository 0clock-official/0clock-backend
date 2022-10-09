package com.oclock.oclock.exception;

import com.oclock.oclock.dto.response.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class OClockExceptionHandler {
    @ExceptionHandler(OClockException.class)
    public ResponseEntity<ErrorMessage> handle(OClockException e, HttpServletRequest request){
        ErrorMessage errorMessage = e.getErrorMessage();
        errorMessage.setRequestId(request.hashCode());
        return ResponseEntity.status(errorMessage.getCode()).body(errorMessage);
    }
}
