package edu.java.scrapper.telegrambotclient.dto.errorresponses;

import java.util.List;

public record BotApiErrorResponse(
    String description,
    String code,
    String exceptionName,
    String exceptionMessage,
    List<String> stacktrace
) implements BotApiErrorResponseBody {
}
