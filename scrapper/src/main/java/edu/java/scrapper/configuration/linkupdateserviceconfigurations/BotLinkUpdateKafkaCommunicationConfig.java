package edu.java.scrapper.configuration.linkupdateserviceconfigurations;

import edu.java.scrapper.botlinkupdateservices.BotLinkUpdateMessageQueueService;
import edu.java.scrapper.botlinkupdateservices.BotLinkUpdateService;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
public class BotLinkUpdateKafkaCommunicationConfig {

    private final ApplicationConfig applicationConfig;
    private final KafkaTemplate<String, LinkUpdate> template;

    @Autowired
    public BotLinkUpdateKafkaCommunicationConfig(
        ApplicationConfig applicationConfig,
        KafkaTemplate<String, LinkUpdate> template
    ) {
        this.applicationConfig = applicationConfig;
        this.template = template;
    }

    @Bean
    public BotLinkUpdateService botLinkUpdateService() {
        return new BotLinkUpdateMessageQueueService(applicationConfig.kafkaSettings().linkUpdateTopic(), template);
    }

}
