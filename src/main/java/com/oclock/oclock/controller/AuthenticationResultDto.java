package com.oclock.oclock.controller;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.security.AuthenticationResult;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import static org.springframework.beans.BeanUtils.copyProperties;
@Getter
@Setter
public class AuthenticationResultDto {

    private String apiToken;

    private Member member;

    public AuthenticationResultDto(AuthenticationResult source) {
        copyProperties(source, this);

        this.member = new Member(source.getMember());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("apiToken", apiToken)
                .append("user", member)
                .toString();
    }

}