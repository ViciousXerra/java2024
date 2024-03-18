package edu.java.scrapper.configuration;

import edu.java.scrapper.schedulers.linkresourceupdaters.AbstractLinkResourceUpdater;
import edu.java.scrapper.schedulers.linkresourceupdaters.GitHubLinkResourceUpdater;
import edu.java.scrapper.schedulers.linkresourceupdaters.StackOverFlowLinkResourceUpdater;
import edu.java.scrapper.webclients.clients.GitHubClient;
import edu.java.scrapper.webclients.clients.StackOverFlowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinkResourceUpdaterConfig {

    @Bean
    AbstractLinkResourceUpdater abstractLinkResourceUpdater(
        GitHubClient gitHubClient,
        StackOverFlowClient stackOverFlowClient
    ) {
        return new GitHubLinkResourceUpdater(
            gitHubClient,
            new StackOverFlowLinkResourceUpdater(
                stackOverFlowClient,
                null
            )
        );
    }

}
