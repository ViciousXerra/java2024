package edu.java.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import java.util.List;

public record QuestionInfo(
    String title,
    List<String> tags,
    StackOverFlowUser owner,
    @JsonProperty("is_answered")
    boolean isAnswered,
    @JsonProperty("answer_count")
    int answerCount,
    @JsonProperty("creation_date")
    OffsetDateTime creationDate,
    @JsonProperty("last_edit_date")
    OffsetDateTime lastEditTime,
    @JsonProperty("last_activity_date")
    OffsetDateTime lastActivityTime
) {
}
