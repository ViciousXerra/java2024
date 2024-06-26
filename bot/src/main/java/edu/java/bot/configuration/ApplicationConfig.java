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
    ClientRetrySettings clientRetrySettings,
    @NotNull
    ApiRateLimitSettings apiRateLimitSettings,
    boolean useQueue,
    @NotNull
    KafkaSettings kafkaSettings
) {

    public record ScrapperSettings(@NotBlank String defaultBaseUrl, String baseUrl) {
    }

    public record ClientRetrySettings(
        @NotNull
        BackoffType backoffType,
        @Positive
        @Max(10)
        int attemptsLimit,
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
        int limit,
        @Positive
        @Max(200)
        int refillLimit,
        @NotNull
        Duration refillDelay
    ) {
    }

    public record KafkaSettings(
        @NotNull
        @NotBlank
        String bootstrapServer,
        @NotNull
        LinkUpdateTopic linkUpdateTopic
    ) {
    }

    public record LinkUpdateTopic(
        @NotNull
        @NotBlank
        String name,
        @NotNull
        @NotBlank
        String consumerGroupId,
        @NotNull
        @NotBlank
        String autoOffsetReset
    ) {
    }

}
