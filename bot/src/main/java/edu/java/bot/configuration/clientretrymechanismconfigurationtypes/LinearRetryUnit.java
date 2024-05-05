package edu.java.bot.configuration.clientretrymechanismconfigurationtypes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
@Getter
class LinearRetryUnit {

    private final Retry.RetrySignal retrySignal;
    private final int attempt;

}
