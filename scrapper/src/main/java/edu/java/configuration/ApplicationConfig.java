package edu.java.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    @NotNull
    StackOverFlowSettings stackOverFlowSettings,
    @NotNull
    GitHubSettings gitHubSettings
) {

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
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
