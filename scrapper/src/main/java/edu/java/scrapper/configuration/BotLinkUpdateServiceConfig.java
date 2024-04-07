package edu.java.scrapper.configuration;

import edu.java.scrapper.botlinkupdateservices.BotLinkUpdateClientService;
import edu.java.scrapper.botlinkupdateservices.BotLinkUpdateMessageQueueService;
import edu.java.scrapper.botlinkupdateservices.BotLinkUpdateService;
import edu.java.scrapper.telegrambotclient.clients.BotUpdateClient;
import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class BotLinkUpdateServiceConfig {

    private final ApplicationConfig applicationConfig;
    private final KafkaTemplate<String, LinkUpdate> template;
    private final BotUpdateClient botUpdateClient;

    @Autowired
    public BotLinkUpdateServiceConfig(
        ApplicationConfig applicationConfig,
        KafkaTemplate<String, LinkUpdate> template,
        BotUpdateClient botUpdateClient
    ) {
        this.applicationConfig = applicationConfig;
        this.template = template;
        this.botUpdateClient = botUpdateClient;
    }

    @Bean
    public BotLinkUpdateService botLinkUpdateService() {
        return applicationConfig.useQueue()
            ? new BotLinkUpdateMessageQueueService(applicationConfig.kafkaSettings().linkUpdateTopic(), template)
            : new BotLinkUpdateClientService(botUpdateClient);
    }

}
