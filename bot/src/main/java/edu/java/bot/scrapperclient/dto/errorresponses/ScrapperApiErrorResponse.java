package edu.java.bot.scrapperclient.dto.errorresponses;

import java.util.List;

public record ScrapperApiErrorResponse(
    String description,
    String code,
    String exceptionName,
    String exceptionMessage,
    List<String> stacktrace
) implements ScrapperApiResponseBody {
}
