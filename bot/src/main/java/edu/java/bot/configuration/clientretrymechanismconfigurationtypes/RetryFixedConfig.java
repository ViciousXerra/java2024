package edu.java.bot.configuration.clientretrymechanismconfigurationtypes;

import edu.java.bot.configuration.ApplicationConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@Configuration
@ConditionalOnProperty(prefix = "app.client-retry-settings", name = "backoff-type", havingValue = "fixed")
@Log4j2
public class RetryFixedConfig {

    private ApplicationConfig applicationConfig;

    @Autowired
    public RetryFixedConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    public RetryBackoffSpec retryBackoffSpec() {
        return Retry.fixedDelay(
                applicationConfig.clientRetrySettings().attemptsLimit(),
                applicationConfig.clientRetrySettings().attemptDelay()
            )
            .filter(throwable -> {
                if (throwable instanceof WebClientResponseException) {
                    return applicationConfig.clientRetrySettings().retryCodes()
                        .contains(((WebClientResponseException) throwable).getStatusCode().value());
                }
                return false;
            })
            .doBeforeRetry(retrySignal -> log.warn(
                "Retrying request with fixed delay scenario after following exception: {}",
                retrySignal.failure().getLocalizedMessage()
            ))
            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> retrySignal.failure());
    }

}
