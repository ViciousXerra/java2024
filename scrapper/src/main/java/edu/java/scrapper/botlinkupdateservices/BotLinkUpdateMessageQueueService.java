package edu.java.scrapper.botlinkupdateservices;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Log4j2
public class BotLinkUpdateMessageQueueService implements BotLinkUpdateService {

    private final ApplicationConfig.LinkUpdateTopic linkUpdateTopic;
    private final KafkaTemplate<String, LinkUpdate> template;

    @Override
    public void postLinkUpdate(LinkUpdate linkUpdate) {
        CompletableFuture<SendResult<String, LinkUpdate>> futureResult =
            template.send(linkUpdateTopic.name(), linkUpdate);
        futureResult.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("Unable to send message in message queue: {}", throwable.getMessage());
            }
        });
    }

}
