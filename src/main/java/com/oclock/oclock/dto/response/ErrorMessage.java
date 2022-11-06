package com.oclock.oclock.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
public class ErrorMessage {
    @Builder.Default
    private String requestId = UUID.randomUUID().toString();
    private int code;
    private String message;
}
