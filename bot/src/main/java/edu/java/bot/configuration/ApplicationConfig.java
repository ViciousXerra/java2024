package edu.java.bot.configuration;

import edu.java.bot.configuration.clientretrymechanismconfigurationtypes.BackoffType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty
    String telegramToken,
    @NotNull
    ScrapperSettings scrapperSettings,
    @NotNull
    ClientRetrySettings clientRetrySettings
) {

    public record ScrapperSettings(@NotBlank String defaultBaseUrl, String baseUrl) {
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

}
