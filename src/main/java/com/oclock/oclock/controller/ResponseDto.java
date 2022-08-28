package com.oclock.oclock.controller;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseDto<T> {
    private boolean success;
    private String response;
    private T data;
}
