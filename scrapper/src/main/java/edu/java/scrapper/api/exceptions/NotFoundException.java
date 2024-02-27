package edu.java.scrapper.api.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends RuntimeException {

    private final String description;
    private final HttpStatus statusCode = HttpStatus.NOT_FOUND;

    public NotFoundException(String message, String description) {
        super(message);
        this.description = description;
    }

}
