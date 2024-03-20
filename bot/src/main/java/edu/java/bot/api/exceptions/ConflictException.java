package edu.java.bot.api.exceptions;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private final String description;

    public ConflictException(String message, String description) {
        super(message);
        this.description = description;
    }

}
