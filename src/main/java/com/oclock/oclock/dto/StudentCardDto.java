package com.oclock.oclock.dto;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class StudentCardDto {
    private String email;

    private String fileName;
    private String base64img;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append(email)
                .append(base64img).toString();
    }
}
