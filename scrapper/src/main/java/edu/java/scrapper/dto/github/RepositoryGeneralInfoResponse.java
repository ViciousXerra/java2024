package edu.java.scrapper.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record RepositoryGeneralInfoResponse(
    long id,
    @JsonProperty("full_name")
    String repositoryName,
    GitHubUser owner,
    @JsonProperty("created_at")
    OffsetDateTime createdAt,
    @JsonProperty("updated_at")
    OffsetDateTime updatedAt,
    @JsonProperty("pushed_at")
    OffsetDateTime pushedAt,
    @JsonProperty("subscribers_count")
    int subscribersCount,
    RepositoryVisibility visibility
) {
}
