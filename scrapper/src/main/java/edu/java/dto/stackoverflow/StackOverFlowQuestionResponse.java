package edu.java.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StackOverFlowQuestionResponse<T>(
    List<T> items,
    @JsonProperty("has_more")
    boolean hasMore,
    @JsonProperty("quota_max")
    int maxQuota,
    @JsonProperty("quota_remaining")
    int remainingQuota
) {
}
