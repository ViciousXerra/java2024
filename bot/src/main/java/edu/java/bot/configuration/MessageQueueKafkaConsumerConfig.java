package edu.java.bot.configuration;

import edu.java.bot.commandexecutors.LinkUpdateCommandExecutor;
import edu.java.bot.messagequeueconsumers.LinkUpdateMessageQueueConsumerService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
public class MessageQueueKafkaConsumerConfig {

    @Bean
    public LinkUpdateMessageQueueConsumerService linkUpdateMessageQueueConsumerService(
        LinkUpdateCommandExecutor linkUpdateCommandExecutor
    ) {
        return new LinkUpdateMessageQueueConsumerService(linkUpdateCommandExecutor);
    }

}
