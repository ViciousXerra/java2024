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
    AbstractLinkResourceUpdater abstractLinkResourceUpdater(GitHubLinkResourceUpdater gitHubLinkResourceUpdater) {
        return gitHubLinkResourceUpdater;
    }

    @Bean
    GitHubLinkResourceUpdater gitHubLinkResourceUpdater(
        StackOverFlowLinkResourceUpdater stackOverFlowResourceUpdater,
        GitHubClient gitHubClient
    ) {
        return new GitHubLinkResourceUpdater(stackOverFlowResourceUpdater, gitHubClient);
    }

    @Bean
    StackOverFlowLinkResourceUpdater stackOverFlowResourceUpdater(
        StackOverFlowClient stackOverFlowClient
    ) {
        return new StackOverFlowLinkResourceUpdater(null, stackOverFlowClient);
    }

}
