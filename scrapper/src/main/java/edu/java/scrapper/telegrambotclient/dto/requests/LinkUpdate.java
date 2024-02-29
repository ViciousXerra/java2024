package edu.java.scrapper.telegrambotclient.dto.requests;

import java.util.List;

public record LinkUpdate(
    long id,
    String url,
    String description,
    List<Long> tgChatIds
) {
}
