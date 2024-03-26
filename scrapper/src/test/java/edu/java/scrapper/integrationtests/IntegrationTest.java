package edu.java.scrapper.integrationtests;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
public abstract class IntegrationTest {

    @ServiceConnection
    static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("scrapper")
        .withUsername("postgres")
        .withPassword("postgres");

    static {
        POSTGRES.start();
        runMigrations(POSTGRES);
    }

    @DynamicPropertySource
    static void stubDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> POSTGRES.getJdbcUrl());
        registry.add("spring.datasource.username", () -> POSTGRES.getUsername());
        registry.add("spring.datasource.password", () -> POSTGRES.getPassword());
    }

    private static void runMigrations(JdbcDatabaseContainer<?> c) {
        try (Connection connection = DriverManager.getConnection(c.getJdbcUrl(), c.getUsername(), c.getPassword())) {
            var changeLogPath = new File(".")
                .toPath().toAbsolutePath().getParent().getParent().resolve("migrations");
            Database db =
                DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase
                liquibase = new Liquibase("master.xml", new DirectoryResourceAccessor(changeLogPath), db);
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (SQLException | LiquibaseException | FileNotFoundException e) {
            throw new RuntimeException("Error occurs in migration process", e);
        }
    }

}
