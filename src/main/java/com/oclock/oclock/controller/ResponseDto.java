package com.oclock.oclock.controller;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseDto<T> {
    private String code;
    private String response;
    private T data;
}
