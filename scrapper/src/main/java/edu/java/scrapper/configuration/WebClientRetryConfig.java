package edu.java.scrapper.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Configuration
public class WebClientRetryConfig {

    private Retry retry;

    @Autowired
    public WebClientRetryConfig(Retry retry) {
        this.retry = retry;
    }

    @Bean
    public ExchangeFilterFunction withRetryableRequests() {
        return (request, next) -> next.exchange(request)
            .flatMap(clientResponse -> Mono.just(clientResponse)
                .filter(response ->  clientResponse.statusCode().is5xxServerError())
                .flatMap(response -> clientResponse.createException())
                .flatMap(Mono::error)
                .thenReturn(clientResponse))
            .retryWhen(retry);
    }

}
