package edu.java.scrapper.configuration;

import edu.java.scrapper.telegrambotclient.ClientException;
import edu.java.scrapper.telegrambotclient.clients.BotUpdateClient;
import edu.java.scrapper.telegrambotclient.dto.errorresponses.BotApiErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
public class BotClientConfig {

    private final ApplicationConfig applicationConfig;

    @Autowired
    public BotClientConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    public BotUpdateClient botUpdateClient() {
        String baseUrl = applicationConfig.botSettings().baseUrl();
        return baseUrl == null || isInvalidBaseUrl(baseUrl)
            ? createClient(BotUpdateClient.class, applicationConfig.botSettings().defaultBaseUrl())
            : createClient(BotUpdateClient.class, baseUrl);
    }

    private static <T> T createClient(Class<T> clientClass, String baseUrl) {
        WebClient webClient = WebClient
            .builder()
            .baseUrl(baseUrl)
            .defaultStatusHandler(
                HttpStatusCode::is4xxClientError,
                clientResponse -> clientResponse
                    .bodyToMono(BotApiErrorResponse.class)
                    .flatMap(
                        body -> Mono.error(new ClientException(body))
                    )
            )
            .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(clientClass);
    }

    private static boolean isInvalidBaseUrl(String url) {
        return url.isBlank();
    }

}
