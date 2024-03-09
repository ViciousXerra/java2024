package edu.java.scrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class IntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("scrapper")
        .withUsername("postgres")
        .withPassword("postgres");

    @Autowired
    protected DataSource dataSource;

    @TestConfiguration
    static class TestConfig {

        @Bean
        DataSource dataSource() {
            return DataSourceBuilder
                .create()
                .url(POSTGRES.getJdbcUrl())
                .username(POSTGRES.getUsername())
                .password(POSTGRES.getPassword())
                .build();
        }

    }

    static {
        POSTGRES.start();
        runMigrations(POSTGRES);
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
