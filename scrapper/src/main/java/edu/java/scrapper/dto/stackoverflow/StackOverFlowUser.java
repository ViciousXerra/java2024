package edu.java.scrapper.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StackOverFlowUser(
    @JsonProperty("display_name")
    String userName,
    int reputation
) {
}
