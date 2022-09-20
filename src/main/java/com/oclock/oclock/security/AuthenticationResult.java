package com.oclock.oclock.security;

import com.oclock.oclock.dto.Member;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
public class AuthenticationResult {
    private final String accessToken;

    private final String refreshToken;

    private final Member member;

    public AuthenticationResult(String accessToken, String refreshToken, Member member) {
        checkArgument(accessToken != null, "apiToken must be provided.");
        checkArgument(refreshToken != null, "apiToken must be provided.");
        checkArgument(member != null, "user must be provided.");

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("accessToken", accessToken)
                .append("refreshToken", refreshToken)
                .append("Member", member)
                .toString();
    }
}
