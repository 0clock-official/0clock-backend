package com.oclock.oclock.config;

import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        System.out.println(rawPassword.toString());
        return true;
    }
}
