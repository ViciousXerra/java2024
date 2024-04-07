package edu.java.bot.messagequeueconsumers;

import edu.java.bot.api.dto.requests.LinkUpdate;
import edu.java.bot.commandexecutors.LinkUpdateCommandExecutor;
import java.net.SocketTimeoutException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class LinkUpdateMessageQueueConsumerService {

    private final LinkUpdateCommandExecutor linkUpdateCommandExecutor;

    @Autowired
    public LinkUpdateMessageQueueConsumerService(LinkUpdateCommandExecutor linkUpdateCommandExecutor) {
        this.linkUpdateCommandExecutor = linkUpdateCommandExecutor;
    }

    @RetryableTopic(
        backoff = @Backoff(delay = 500, multiplier = 2.0),
        kafkaTemplate = "retryableTopicKafkaTemplate",
        include = {MessagingException.class, SocketTimeoutException.class},
        retryTopicSuffix = "_retry",
        dltTopicSuffix = "_dlq"
    )
    @KafkaListener(groupId = "${app.kafka-settings.link-update-topic.consumer-group-id}",
                   topics = "${app.kafka-settings.link-update-topic.name}",
                   containerFactory = "kafkaListenerContainerFactory")
    public void handleLinkUpdate(LinkUpdate linkUpdate, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        linkUpdateCommandExecutor.process(linkUpdate);
    }

    @DltHandler
    public void handleDltLinkUpdate(LinkUpdate linkUpdate, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        if (linkUpdate != null) {
            log.error("Event on topic={}, payload={}", topic, linkUpdate);
        }
    }

}
