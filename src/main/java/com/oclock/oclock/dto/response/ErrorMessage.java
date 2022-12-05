package com.oclock.oclock.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
public class ErrorMessage {
    @Setter
    private String requestId;
    private int code;
    private String message;
}
