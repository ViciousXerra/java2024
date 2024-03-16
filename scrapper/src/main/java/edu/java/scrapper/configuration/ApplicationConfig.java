package edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
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
    BotSettings botSettings
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

}
