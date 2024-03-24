package edu.java.scrapper.webclients.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StackOverFlowUser(
    @JsonProperty("display_name")
    String userName,
    int reputation
) {
}
