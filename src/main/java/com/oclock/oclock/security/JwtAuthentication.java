package com.oclock.oclock.security;

import com.oclock.oclock.dto.Member;
import com.oclock.oclock.model.Email;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


import static com.google.common.base.Preconditions.checkArgument;

public class JwtAuthentication {
    public final Long id;

    JwtAuthentication(Long id) {
        checkArgument(id != null, "id must be provided.");
        this.id = id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("userKey", id)
                .toString();
    }
}
