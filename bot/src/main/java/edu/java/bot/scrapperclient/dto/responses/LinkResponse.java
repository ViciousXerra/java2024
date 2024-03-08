package edu.java.bot.scrapperclient.dto.responses;

import java.net.URI;

public record LinkResponse(
    long id,
    URI url
) {
}
