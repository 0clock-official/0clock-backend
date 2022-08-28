package com.oclock.oclock.dto;

public class NewMemberDto {

    private final String principal;
    private final String credentials;

    public NewMemberDto(String principal, String credentials) {
        this.principal = principal;
        this.credentials = credentials;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getCredentials() {
        return credentials;
    }
}
