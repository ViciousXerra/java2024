package edu.java.scrapper.api.exceptions;

import lombok.Getter;

@Getter
public class RateLimitException extends RuntimeException {

    private final String description;

    public RateLimitException(String message, String description) {
        super(message);
        this.description = description;
    }

}
