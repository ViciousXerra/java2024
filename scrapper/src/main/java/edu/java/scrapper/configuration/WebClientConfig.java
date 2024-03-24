package edu.java.scrapper.configuration;

import edu.java.scrapper.webclients.clients.GitHubClient;
import edu.java.scrapper.webclients.clients.StackOverFlowClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebClientConfig {

    private final ApplicationConfig applicationConfig;

    @Autowired
    public WebClientConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    public GitHubClient gitHubClient() {
        WebClient webClient = WebClient.builder().baseUrl(resolveGitHubBaseUrl()).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(GitHubClient.class);
    }

    @Bean
    public StackOverFlowClient stackOverFlowClient() {
        WebClient webClient = WebClient.builder().baseUrl(resolveStackOverFlowBaseUrl()).build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(StackOverFlowClient.class);
    }

    private String resolveGitHubBaseUrl() {
        String baseUrl = applicationConfig.gitHubSettings().baseUrl();
        if (isInvalidBaseUrl(baseUrl)) {
            return applicationConfig.gitHubSettings().defaultBaseUrl();
        }
        return baseUrl;
    }

    private String resolveStackOverFlowBaseUrl() {
        String baseUrl = applicationConfig.stackOverFlowSettings().baseUrl();
        if (isInvalidBaseUrl(baseUrl)) {
            return applicationConfig.stackOverFlowSettings().defaultBaseUrl();
        }
        return baseUrl;
    }

    private static boolean isInvalidBaseUrl(String url) {
        return url == null || url.isEmpty() || url.isBlank();
    }

}
