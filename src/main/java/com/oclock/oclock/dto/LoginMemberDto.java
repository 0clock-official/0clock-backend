package com.oclock.oclock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginMemberDto {

    private final String principal;
    private final String credentials;
}
