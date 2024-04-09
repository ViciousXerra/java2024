package edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotEmpty;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.datasource", ignoreUnknownFields = false)
public record DataSourceConfig(
    @NotEmpty
    String url,
    @NotEmpty
    String username,
    @NotEmpty
    String password
) {
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder
            .create()
            .url(url)
            .username(username)
            .password(password)
            .build();
    }

    @Bean
    public JdbcClient jdbcClient(DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }

}
