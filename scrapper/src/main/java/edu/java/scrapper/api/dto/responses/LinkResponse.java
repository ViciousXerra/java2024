package edu.java.scrapper.api.dto.responses;

import java.net.URI;

public record LinkResponse(
    long id,
    URI url
) {
}
