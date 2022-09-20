package com.oclock.oclock.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;


public class TestConfig {

    public DataSource dataSource() {
        return Mockito.mock(DataSource.class);
    }
}
