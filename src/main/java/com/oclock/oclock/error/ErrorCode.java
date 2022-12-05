package com.oclock.oclock.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    NOT_FOUND(404,"USER-ERROR-404", "해당유저없음");

    private final int status;
    private final String errorCode;
    private final String message;
}
