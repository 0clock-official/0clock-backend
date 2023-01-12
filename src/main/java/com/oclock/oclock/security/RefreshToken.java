package com.oclock.oclock.security;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RefreshToken {
    private Long id;
    private String refreshToken;
}
