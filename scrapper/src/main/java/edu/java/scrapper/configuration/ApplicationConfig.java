package edu.java.scrapper.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    BotSettings botSettings,
    @NotNull
    StackOverFlowSettings stackOverFlowSettings,
    @NotNull
    GitHubSettings gitHubSettings
) {

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
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
