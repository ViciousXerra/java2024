package edu.java.scrapper.configuration.kafkaconfiguration;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.configuration.kafkaconfiguration.serializers.LinkUpdateSerializer;
import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "use-queue", havingValue = "true")
public class KafkaConfig {

    private final ApplicationConfig applicationConfig;

    @Autowired
    public KafkaConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    public NewTopic linkUpdateTopic() {
        return
            TopicBuilder
                .name(applicationConfig.kafkaSettings().linkUpdateTopic().name())
                .partitions(applicationConfig.kafkaSettings().linkUpdateTopic().partitions())
                .replicas(applicationConfig.kafkaSettings().linkUpdateTopic().replicas())
                .build();
    }

    @Bean
    public KafkaTemplate<String, LinkUpdate> kafkaTemplate(ProducerFactory<String, LinkUpdate> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, LinkUpdate> producerFactory() {
        return new DefaultKafkaProducerFactory<>(senderProps());
    }

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafkaSettings().bootstrapServer());
        props.put(ProducerConfig.LINGER_MS_CONFIG, applicationConfig.kafkaSettings().lingerMs());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, applicationConfig.kafkaSettings().batchSize());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LinkUpdateSerializer.class);
        return props;
    }

}
