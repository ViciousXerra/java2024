package edu.java.scrapper.configuration.linkupdateserviceconfigurations;

import edu.java.scrapper.botlinkupdateservices.BotLinkUpdateClientService;
import edu.java.scrapper.botlinkupdateservices.BotLinkUpdateService;
import edu.java.scrapper.telegrambotclient.clients.BotUpdateClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "false")
public class BotLinkUpdateApiCommunicationConfig {

    private final BotUpdateClient botUpdateClient;

    @Autowired
    public BotLinkUpdateApiCommunicationConfig(BotUpdateClient botUpdateClient) {
        this.botUpdateClient = botUpdateClient;
    }

    @Bean
    public BotLinkUpdateService botLinkUpdateService() {
        return new BotLinkUpdateClientService(botUpdateClient);
    }

}
