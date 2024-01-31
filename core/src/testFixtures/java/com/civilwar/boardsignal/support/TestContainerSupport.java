package com.civilwar.boardsignal.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TestContainerSupport {

    private static final String MYSQL_IMAGE = "mysql:8.0";

    private static final int MYSQL_PORT = 3306;

    private static final JdbcDatabaseContainer<?> MYSQL;

    static {
        MYSQL = new MySQLContainer<>(DockerImageName.parse(MYSQL_IMAGE))
            .withExposedPorts(MYSQL_PORT)
            .withReuse(true);

        MYSQL.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }
}
