package edu.java.scrapper.configuration;

import edu.java.scrapper.telegrambotclient.ClientException;
import edu.java.scrapper.telegrambotclient.clients.BotUpdateClient;
import edu.java.scrapper.telegrambotclient.dto.errorresponses.BotApiErrorResponse;
import edu.java.scrapper.webclients.clients.GitHubClient;
import edu.java.scrapper.webclients.clients.StackOverFlowClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    private final ApplicationConfig applicationConfig;
    private final ExchangeFilterFunction exchangeFilterFunction;

    @Autowired
    public WebClientConfig(ApplicationConfig applicationConfig, ExchangeFilterFunction exchangeFilterFunction) {
        this.applicationConfig = applicationConfig;
        this.exchangeFilterFunction = exchangeFilterFunction;
    }

    @Bean
    public GitHubClient gitHubClient() {
        String baseUrl = applicationConfig.gitHubSettings().baseUrl();
        return baseUrl == null || isInvalidBaseUrl(baseUrl) ? createClientWithBodyToMonoException(
            GitHubClient.class,
            applicationConfig.gitHubSettings().defaultBaseUrl(),
            exchangeFilterFunction
        ) : createClientWithBodyToMonoException(GitHubClient.class, baseUrl, exchangeFilterFunction);
    }

    @Bean
    public StackOverFlowClient stackOverFlowClient() {
        String baseUrl = applicationConfig.stackOverFlowSettings().baseUrl();
        return baseUrl == null || isInvalidBaseUrl(baseUrl) ? createClientWithBodyToMonoException(
            StackOverFlowClient.class,
            applicationConfig.stackOverFlowSettings().defaultBaseUrl(),
            exchangeFilterFunction
        ) : createClientWithBodyToMonoException(StackOverFlowClient.class, baseUrl, exchangeFilterFunction);
    }

    @Bean
    public BotUpdateClient botUpdateClient() {
        String baseUrl = applicationConfig.botSettings().baseUrl();
        return baseUrl == null || isInvalidBaseUrl(baseUrl) ? createClientWithBodyToMonoResponse(
            BotUpdateClient.class,
            applicationConfig.botSettings().defaultBaseUrl(),
            exchangeFilterFunction
        ) : createClientWithBodyToMonoResponse(BotUpdateClient.class, baseUrl, exchangeFilterFunction);
    }

    private static <T> T createClientWithBodyToMonoException(
        Class<T> clientClass,
        String baseUrl,
        ExchangeFilterFunction exchangeFilterFunction
    ) {
        WebClient webClient = WebClient
            .builder()
            .baseUrl(baseUrl)
            .filter(exchangeFilterFunction)
            .defaultStatusHandler(
                HttpStatusCode::is4xxClientError,
                ClientResponse::createException
            )
            .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(clientClass);
    }

    private static <T> T createClientWithBodyToMonoResponse(
        Class<T> clientClass,
        String baseUrl,
        ExchangeFilterFunction exchangeFilterFunction
    ) {
        WebClient webClient = WebClient
            .builder()
            .baseUrl(baseUrl)
            .filter(exchangeFilterFunction)
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
