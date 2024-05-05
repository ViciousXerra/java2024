package edu.java.bot.configuration.kafkaconfiguration.deserializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.api.dto.requests.LinkUpdate;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Deserializer;

@Log4j2
public class LinkUpdateDeserializer implements Deserializer<LinkUpdate> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public LinkUpdate deserialize(String s, byte[] bytes) {
        if (bytes == null) {
            log.error("Null received at deserializing");
            return null;
        }
        try {
            return objectMapper.readValue(bytes, LinkUpdate.class);
        } catch (Exception e) {
            log.error("Unable to deserialize message: {}", e.getMessage());
            return null;
        }
    }

}
