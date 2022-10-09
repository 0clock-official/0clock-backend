package com.oclock.oclock.exception;

import com.oclock.oclock.dto.response.ErrorMessage;
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
    private ErrorMessage errorMessage;
    public OClockException(ErrorMessage errorMessage){
        this.errorMessage = errorMessage;
    }
}
