package com.oclock.oclock.exception;

import com.oclock.oclock.dto.response.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class OClockExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handle(Exception e, HttpServletRequest request){
        if(e instanceof OClockException) {
            OClockException oClockException = (OClockException)e;
            ErrorMessage errorMessage = oClockException.getErrorMessage();
            return ResponseEntity.status(errorMessage.getCode()).body(errorMessage);
        }else{
            log.error(e.toString());
            ErrorMessage errorMessage = ErrorMessage.builder()
                    .message("알수 없는 이유로 요청이 정상처리 되지 않았습니다.")
                    .code(500)
                    .build();
            return ResponseEntity.status(errorMessage.getCode()).body(errorMessage);
        }
    }
}
