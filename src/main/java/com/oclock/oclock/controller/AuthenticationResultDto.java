package com.oclock.oclock.controller;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.security.AuthenticationResult;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import static org.springframework.beans.BeanUtils.copyProperties;
public class AuthenticationResultDto {

    private String apiToken;

    private Member member;

    public AuthenticationResultDto(AuthenticationResult source) {
        copyProperties(source, this);

        this.member = new Member(source.getMember());
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public Member getUser() {
        return member;
    }

    public void setUser(Member member) {
        this.member = member;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("apiToken", apiToken)
                .append("user", member)
                .toString();
    }

}