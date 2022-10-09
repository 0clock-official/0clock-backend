package com.oclock.oclock.exception;

import com.oclock.oclock.dto.response.ErrorMessage;
import lombok.Getter;

public class ChattingException extends RuntimeException{
    @Getter
    private ErrorMessage errorMessage;
    public ChattingException(ErrorMessage errorMessage){
        this.errorMessage = errorMessage;
    }
}
