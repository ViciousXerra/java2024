package edu.java.scrapper.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record AnswerInfo(
    StackOverFlowUser owner,
    @JsonProperty("is_accepted")
    boolean isAccepted,
    int score,
    @JsonProperty("creation_date")
    OffsetDateTime creationTime,
    @JsonProperty("last_edit_date")
    OffsetDateTime lastEditTime,
    @JsonProperty("last_activity_date")
    OffsetDateTime lastActivityTime
) {
}
