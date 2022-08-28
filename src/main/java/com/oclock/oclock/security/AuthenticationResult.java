package com.oclock.oclock.security;

import com.oclock.oclock.dto.Member;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
public class AuthenticationResult {
    private final String apiToken;

    private final Member member;

    public AuthenticationResult(String apiToken, Member member) {
        checkArgument(apiToken != null, "apiToken must be provided.");
        checkArgument(member != null, "user must be provided.");

        this.apiToken = apiToken;
        this.member = member;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("apiToken", apiToken)
                .append("Member", member)
                .toString();
    }
}
