package com.oclock.oclock.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.google.common.base.Preconditions.checkArgument;

@Builder
@Getter
public class JoinResult {
    @ApiModelProperty(value = "API 토큰", required = true)
    private final String apiToken;
    @ApiModelProperty(value = "사용자 정보", required = true)
    private final Member member;

    public JoinResult(String apiToken, Member member) {
        checkArgument(apiToken != null, "apiToken must be provided.");
        checkArgument(member != null, "user must be provided.");

        this.apiToken = apiToken;
        this.member = member;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("apiToken", apiToken)
                .append("member", member)
                .toString();
    }
}
