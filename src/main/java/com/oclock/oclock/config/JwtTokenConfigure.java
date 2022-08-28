package com.oclock.oclock.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Configuration
public class JwtTokenConfigure {

    private String header;

    private String issuer;

    private String clientSecret = "ENC(4A3olL6mZa+WwBbSd0IBUpiwjoQhC5s4wZcKWYsRzfPr5/KuHtgSwxafwdlVpTdXOxVuOB3a1iY=)";

    private int expirySeconds;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("header", header)
                .append("issuer", issuer)
                .append("clientSecret", clientSecret)
                .append("expirySeconds", expirySeconds)
                .toString();
    }
}
