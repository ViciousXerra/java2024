package edu.java.scrapper.linkupdatemessagequeueservicetests;

import edu.java.scrapper.botlinkupdateservices.BotLinkUpdateService;
import edu.java.scrapper.configuration.kafkaconfiguration.deserializers.LinkUpdateDeserializer;
import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class KafkaLinkUpdateProducerTest {

    private static final String CONSUMER_GROUP_ID = "group";
    private static final String TOPIC_NAME = "link_updates";
    private static final long POLL_DATA_DELAY = 500;
    @ServiceConnection
    private static final KafkaContainer KAFKA =
        new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.2"));

    static {
        KAFKA.start();
    }

    @Autowired
    private BotLinkUpdateService botLinkUpdateService;
    @Autowired
    private KafkaConsumer<String, LinkUpdate> consumer;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public KafkaConsumer<String, LinkUpdate> kafkaConsumer() {
            return new KafkaConsumer<>(consumerProps());
        }

        private Map<String, Object> consumerProps() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LinkUpdateDeserializer.class);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            return props;
        }
    }

    @DynamicPropertySource
    static void stubBootstrapServer(DynamicPropertyRegistry registry) {
        registry.add("app.use-queue", () -> true);
        registry.add("app.kafka-settings.bootstrap-server", KAFKA::getBootstrapServers);
    }

    @Test
    @DisplayName("Test link update message consume")
    public void testMessageConsume() {
        //Set up
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));
        //Given
        LinkUpdate expectedlinkUpdate = new LinkUpdate(1L, "test_url", "test_desc", List.of(1L, 2L, 3L));
        //When
        botLinkUpdateService.postLinkUpdate(expectedlinkUpdate);
        Iterable<ConsumerRecord<String, LinkUpdate>> records =
            consumer.poll(Duration.ofMillis(POLL_DATA_DELAY)).records(TOPIC_NAME);
        List<LinkUpdate> actualConsumedRecords = new ArrayList<>();
        records.forEach(r -> actualConsumedRecords.addFirst(r.value()));
        //Then
        Assertions.assertAll(
            () -> assertThat(actualConsumedRecords).isNotEmpty(),
            () -> assertThat(actualConsumedRecords.size()).isEqualTo(1L),
            () -> assertThat(actualConsumedRecords.getFirst()).isEqualTo(expectedlinkUpdate)
        );
    }

}
