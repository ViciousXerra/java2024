package edu.java.bot.messagequeueconsumertests;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.api.dto.requests.LinkUpdate;
import edu.java.bot.commandexecutors.LinkUpdateCommandExecutor;
import edu.java.bot.configuration.kafkaconfiguration.serializers.LinkUpdateSerializer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
public class KafkaLinkUpdateConsumerTest {

    private static final String TOPIC_NAME = "link_updates";
    private static final int PARTITIONS = 10;
    private static final int REPLICAS = 1;
    private static final int LINGER_MS = 10;
    private static final int BATCH_SIZE = 10;
    private static final long POLL_DATA_DELAY = 5000;
    @ServiceConnection
    private static final KafkaContainer KAFKA =
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.2"));

    static {
        KAFKA.start();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public NewTopic linkUpdateTopic() {
            return
                TopicBuilder
                    .name(TOPIC_NAME)
                    .partitions(PARTITIONS)
                    .replicas(REPLICAS)
                    .build();
        }

        @Bean
        public KafkaTemplate<String, LinkUpdate> producerKafkaTemplate(
            ProducerFactory<String, LinkUpdate> MessageQueueProducerFactory
        ) {
            return new KafkaTemplate<>(MessageQueueProducerFactory);
        }

        @Bean
        public ProducerFactory<String, LinkUpdate> MessageQueueProducerFactory() {
            return new DefaultKafkaProducerFactory<>(senderProps());
        }

        private Map<String, Object> senderProps() {
            Map<String, Object> props = new HashMap<>();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
            props.put(ProducerConfig.LINGER_MS_CONFIG, LINGER_MS);
            props.put(ProducerConfig.BATCH_SIZE_CONFIG, BATCH_SIZE);
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LinkUpdateSerializer.class);
            return props;
        }

    }

    @DynamicPropertySource
    static void stubBootstrapServer(DynamicPropertyRegistry registry) {
        registry.add("app.use-queue", () -> true);
        registry.add("app.kafka-settings.bootstrap-server", KAFKA::getBootstrapServers);
    }

    @MockBean
    private TelegramBot telegramBot;
    @MockBean
    private LinkUpdateCommandExecutor linkUpdateCommandExecutor;
    private final KafkaTemplate<String, LinkUpdate> producerKafkaTemplate;

    @Autowired
    public KafkaLinkUpdateConsumerTest(KafkaTemplate<String, LinkUpdate> producerKafkaTemplate) {
        this.producerKafkaTemplate = producerKafkaTemplate;
    }

    @Test
    @DisplayName("Test link update message consume")
    public void testMessageConsume() {
        class SignalException extends RuntimeException {
        }
        //Given
        LinkUpdate expectedLinkUpdate = new LinkUpdate(1L, "test_url", "test_desc", List.of(1L, 2L, 3L));
        //When
        Mockito.doAnswer(invocationOnMock -> {
            throw new SignalException();
        }).when(linkUpdateCommandExecutor).process(Mockito.any());
        Mockito.doNothing().when(linkUpdateCommandExecutor).process(expectedLinkUpdate);
        //Then (logs must be clean! No producer actions to link_updates_retry and link_updates_dlq topics)
        Assertions.assertDoesNotThrow(() -> {
            producerKafkaTemplate.send(TOPIC_NAME, expectedLinkUpdate);
            Thread.sleep(POLL_DATA_DELAY);
        });
    }

}
