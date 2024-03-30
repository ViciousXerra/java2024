package edu.java.bot.configuration.clientretrymechanismconfigurationtypes;

import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

class LinearRetry extends Retry {
    private Consumer<RetrySignal> doBeforeRetry;
    private Predicate<? super Throwable> throwablePredicate;
    private BiFunction<LinearRetry, RetrySignal, Throwable> throwableGenerator;
    private final int maxAttempts;
    private Duration startDelay;
    private final Duration maxDelay;

    LinearRetry(int maxAttempts, Duration startDelay, Duration maxDelay) {
        this.maxAttempts = maxAttempts;
        this.startDelay = startDelay;
        this.maxDelay = maxDelay;
    }

    @Override
    public Publisher<?> generateCompanion(Flux<RetrySignal> flux) {
        return flux
            .zipWith(
                Flux.range(1, maxAttempts),
                LinearRetryUnit::new
            ).flatMap(this::processLinearRetryUnit);
    }

    public Publisher<?> processLinearRetryUnit(LinearRetryUnit unit) {
        if (throwablePredicate == null || throwablePredicate.test(unit.getRetrySignal().failure())) {
            return increaseDelay(unit);
        } else {
            return Mono.error(unit.getRetrySignal().failure());
        }
    }

    public Publisher<?> increaseDelay(LinearRetryUnit unit) {
        if (doBeforeRetry != null) {
            doBeforeRetry.accept(unit.getRetrySignal());
        }
        if (unit.getAttempt() < maxAttempts && startDelay.compareTo(maxDelay) < 0) {
            startDelay.multipliedBy(unit.getAttempt());
            if (startDelay.compareTo(maxDelay) > 0) {
                startDelay = maxDelay;
            }
            return Mono.delay(startDelay);
        } else if (unit.getAttempt() < maxAttempts) {
            return Mono.delay(maxDelay);
        } else {
            if (throwableGenerator != null) {
                throw new RetryExhaustedException(
                    "Retry attempts exhausted",
                    throwableGenerator.apply(this, unit.getRetrySignal())
                );
            }
            return Mono.error(unit.getRetrySignal().failure());
        }
    }

    public LinearRetry doBeforeRetry(Consumer<RetrySignal> doBeforeRetry) {
        this.doBeforeRetry = doBeforeRetry;
        return this;
    }

    public LinearRetry filter(Predicate<? super Throwable> throwablePredicate) {
        this.throwablePredicate = throwablePredicate;
        return this;
    }

    public LinearRetry onRetryExhaustedThrow(BiFunction<LinearRetry, RetrySignal, Throwable> throwableGenerator) {
        this.throwableGenerator = throwableGenerator;
        return this;
    }

}
