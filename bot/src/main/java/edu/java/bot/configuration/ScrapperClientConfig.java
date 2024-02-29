package edu.java.bot.configuration;

import edu.java.bot.scrapperclient.clients.ChatClient;
import edu.java.bot.scrapperclient.clients.LinksClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ScrapperClientConfig {

    private final ApplicationConfig applicationConfig;

    @Autowired
    public ScrapperClientConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    public ChatClient chatClient() {
        String baseUrl = applicationConfig.scrapperSettings().baseUrl();
        return isInvalidBaseUrl(baseUrl)
            ? createClient(ChatClient.class, applicationConfig.scrapperSettings().defaultBaseUrl())
            : createClient(ChatClient.class, baseUrl);
    }

    @Bean
    public LinksClient linksClient() {
        String baseUrl = applicationConfig.scrapperSettings().baseUrl();
        return isInvalidBaseUrl(baseUrl)
            ? createClient(LinksClient.class, applicationConfig.scrapperSettings().defaultBaseUrl())
            : createClient(LinksClient.class, baseUrl);
    }

    private static <T> T createClient(Class<T> clientClass, String baseUrl) {
        WebClient webClient = WebClient
            .builder()
            .baseUrl(baseUrl)
            .defaultStatusHandler(
                HttpStatusCode::is4xxClientError,
                ClientResponse::createException
            )
            .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(clientClass);
    }

    private static boolean isInvalidBaseUrl(String url) {
        return url == null || url.isEmpty() || url.isBlank();
    }

}
