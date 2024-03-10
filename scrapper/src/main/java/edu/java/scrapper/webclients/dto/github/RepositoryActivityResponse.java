package edu.java.scrapper.webclients.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record RepositoryActivityResponse(
    long id,
    GitHubUser actor,
    String ref,
    OffsetDateTime timestamp,
    @JsonProperty("activity_type")
    RepositoryActivityType activityType
) {
}
