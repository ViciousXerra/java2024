package edu.java.scrapper.configuration;

import edu.java.scrapper.configuration.clientretrymechanismconfigurationtypes.BackoffType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @Bean
    @NotNull
    Scheduler scheduler,
    @NotNull
    StackOverFlowSettings stackOverFlowSettings,
    @NotNull
    GitHubSettings gitHubSettings,
    @NotNull
    BotSettings botSettings,
    @NotNull
    AccessType databaseAccessType,
    @NotNull
    ClientRetrySettings clientRetrySettings,
    @NotNull
    ApiRateLimitSettings apiRateLimitSettings
) {

    public record Scheduler(
        boolean enable,
        @NotNull Duration interval,
        @NotNull Duration forceCheckDelay,
        @Positive
        int fetchLimit
    ) {
    }

    public record BotSettings(@NotBlank String defaultBaseUrl, String baseUrl) {
    }

    public record StackOverFlowSettings(
        @NotNull
        @NotEmpty
        @NotBlank
        String defaultBaseUrl,
        String baseUrl
    ) {
    }

    public record GitHubSettings(
        @NotNull
        @NotEmpty
        @NotBlank
        String defaultBaseUrl,
        String baseUrl
    ) {
    }

    public enum AccessType {
        JDBC, JOOQ, JPA
    }

    public record ClientRetrySettings(
        @NotNull
        BackoffType backoffType,
        @Positive
        @Max(10)
        Integer attemptsLimit,
        @NotNull
        Duration attemptDelay,
        @NotNull
        Duration attemptDelayLimit,
        @NotEmpty
        List<Integer> retryCodes
    ) {
    }

    public record ApiRateLimitSettings(
        @Positive
        @Max(2000)
        Integer limit,
        @Positive
        @Max(200)
        Integer refillLimit,
        @NotNull
        Duration refillDelay
    ) {
    }

}
