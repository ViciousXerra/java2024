package edu.java.bot.configuration.kafkaconfiguration;

import edu.java.bot.api.dto.requests.LinkUpdate;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.configuration.kafkaconfiguration.deserializers.LinkUpdateDeserializer;
import edu.java.bot.configuration.kafkaconfiguration.serializers.LinkUpdateSerializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaConfig {

    private final ApplicationConfig applicationConfig;

    @Autowired
    public KafkaConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> kafkaListenerContainerFactory(
        ConsumerFactory<String, LinkUpdate> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, LinkUpdate> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public KafkaTemplate<String, LinkUpdate> retryableTopicKafkaTemplate(
        ProducerFactory<String, LinkUpdate> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, LinkUpdate> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    @Bean
    public ProducerFactory<String, LinkUpdate> producerFactory() {
        return new DefaultKafkaProducerFactory<>(senderProps());
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafkaSettings().bootstrapServer());
        props.put(
            ConsumerConfig.GROUP_ID_CONFIG,
            applicationConfig.kafkaSettings().linkUpdateTopic().consumerGroupId()
        );
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LinkUpdateDeserializer.class);
        props.put(
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            applicationConfig.kafkaSettings().linkUpdateTopic().autoOffsetReset()
        );
        return props;
    }

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafkaSettings().bootstrapServer());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LinkUpdateSerializer.class);
        return props;
    }

}
