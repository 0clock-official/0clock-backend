package com.oclock.oclock.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Getter
public class Verification {

    private final String memberEmail;
    private final String verification;
}
