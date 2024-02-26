package edu.java.bot.api.dto.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.net.URI;
import java.util.List;

public record LinkUpdate(
    long id,
    URI url,
    String description,
    List<Long> tgChatIds
) {
}
