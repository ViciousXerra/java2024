package edu.java.bot.configuration;

import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperclient.clients.ChatClient;
import edu.java.bot.scrapperclient.clients.LinksClient;
import edu.java.bot.scrapperclient.dto.errorresponses.ScrapperApiErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
public class ScrapperClientConfig {

    private final ApplicationConfig applicationConfig;
    private final ExchangeFilterFunction exchangeFilterFunction;

    @Autowired
    public ScrapperClientConfig(ApplicationConfig applicationConfig, ExchangeFilterFunction exchangeFilterFunction) {
        this.applicationConfig = applicationConfig;
        this.exchangeFilterFunction = exchangeFilterFunction;
    }

    @Bean
    public ChatClient chatClient() {
        String baseUrl = applicationConfig.scrapperSettings().baseUrl();
        return baseUrl == null || isInvalidBaseUrl(baseUrl)
            ? createClient(ChatClient.class, applicationConfig.scrapperSettings().defaultBaseUrl(), exchangeFilterFunction)
            : createClient(ChatClient.class, baseUrl, exchangeFilterFunction);
    }

    @Bean
    public LinksClient linksClient() {
        String baseUrl = applicationConfig.scrapperSettings().baseUrl();
        return baseUrl == null || isInvalidBaseUrl(baseUrl)
            ? createClient(LinksClient.class, applicationConfig.scrapperSettings().defaultBaseUrl(), exchangeFilterFunction)
            : createClient(LinksClient.class, baseUrl, exchangeFilterFunction);
    }

    private static <T> T createClient(Class<T> clientClass, String baseUrl, ExchangeFilterFunction exchangeFilterFunction) {
        WebClient webClient = WebClient
            .builder()
            .baseUrl(baseUrl)
            .defaultStatusHandler(
                HttpStatusCode::is4xxClientError,
                clientResponse -> clientResponse
                    .bodyToMono(ScrapperApiErrorResponse.class)
                    .flatMap(
                        body -> Mono.error(new ClientException(body))
                    )
            )
            .filter(exchangeFilterFunction)
            .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(clientClass);
    }

    private static boolean isInvalidBaseUrl(String url) {
        return url.isBlank();
    }

}
