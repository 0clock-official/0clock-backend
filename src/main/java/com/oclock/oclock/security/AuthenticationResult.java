package com.oclock.oclock.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
public class AuthenticationResult {
    private final String accessToken;

    private final String refreshToken;



    public AuthenticationResult(String accessToken, String refreshToken) {
        checkArgument(accessToken != null, "apiToken must be provided.");
        checkArgument(refreshToken != null, "apiToken must be provided.");
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("accessToken", accessToken)
                .append("refreshToken", refreshToken)
                .toString();
    }
}
