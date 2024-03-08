package edu.java.bot.scrapperclient.dto.responses;

import java.util.List;

public record ListLinkResponse(
    List<LinkResponse> links,
    int size
) {
}
