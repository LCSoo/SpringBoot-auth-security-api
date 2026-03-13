package com.example.auth_security_system;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.mysql.MySQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public abstract class TestcontainersConfiguration {
    private static final String MYSQL_IMAGE = "mysql:latest";

    static final MySQLContainer MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer(MYSQL_IMAGE);
                
        MYSQL_CONTAINER
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass")
                .start();
    }
}
