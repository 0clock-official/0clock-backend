package com.oclock.oclock.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorMessage {
    private int code;
    private String message;
}
