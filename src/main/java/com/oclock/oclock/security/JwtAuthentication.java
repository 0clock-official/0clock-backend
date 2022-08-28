package com.oclock.oclock.security;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.model.Email;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


import static com.google.common.base.Preconditions.checkArgument;

public class JwtAuthentication {
    public final Long id;
    public final Email email;

    public final Member member;

    JwtAuthentication(Long id, Email email, Member member) {
        checkArgument(id != null, "email must be provided.");
        checkArgument(email != null, "email must be provided.");
        checkArgument(member != null, "member must be provided.");
        this.id = id;
        this.email = email;
        this.member = member;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("email", email)
                .append("member", member)
                .toString();
    }
}
