package edu.java.bot.configuration.kafkaconfiguration.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.api.dto.requests.LinkUpdate;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serializer;

@Log4j2
public class LinkUpdateSerializer implements Serializer<LinkUpdate> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, LinkUpdate linkUpdate) {
        if (linkUpdate == null) {
            log.error("Null received at serializing");
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(linkUpdate);
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize object: {}", e.getMessage());
            return null;
        }
    }

}
