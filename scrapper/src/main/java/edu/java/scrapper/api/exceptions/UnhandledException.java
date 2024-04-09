package edu.java.scrapper.api.exceptions;

import lombok.Getter;

@Getter
public class UnhandledException extends RuntimeException {

    private final String description;

    public UnhandledException(String message, String description) {
        super(message);
        this.description = description;
    }

}
