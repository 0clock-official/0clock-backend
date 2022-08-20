package com.oclock.oclock.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.google.common.base.Preconditions.checkArgument;

@Builder
@Getter
public class JoinRequest {
    @ApiModelProperty(value = "로그인 이메일", required = true)
    private String principal;
    @ApiModelProperty(value = "로그인 비밀번호", required = true)
    private String credentials;

    public JoinRequest() {
    }

    public JoinRequest(String principal, String credentials) {
        checkArgument(principal != null, "principal must be provided.");
        checkArgument(credentials != null, "credentials must be provided.");
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("principal", principal)
                .append("credentials", credentials)
                .toString();
    }
}
