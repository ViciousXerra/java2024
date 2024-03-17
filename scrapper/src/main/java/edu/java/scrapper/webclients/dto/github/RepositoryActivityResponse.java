package edu.java.scrapper.webclients.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import java.time.OffsetDateTime;

public record RepositoryActivityResponse(
    long id,
    GitHubUser actor,
    String ref,
    OffsetDateTime timestamp,
    @JsonProperty("activity_type")
    RepositoryActivityType activityType
) implements Comparable<RepositoryActivityResponse> {

    @Override
    public int compareTo(@NotNull RepositoryActivityResponse o) {
        return this.timestamp().compareTo(o.timestamp());
    }

}
