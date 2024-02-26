package edu.java.scrapper.api.controlleradvices;

import edu.java.scrapper.api.dto.errorresponses.ApiErrorResponse;
import edu.java.scrapper.api.exceptions.ChatNotFoundException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ScrapperControllerAdvice {

    private final static String BAD_REQUEST_DESCRIPTION = "Invalid or incorrect request parameters";
    private final static String NOT_FOUND_DESCRIPTION = "The chat does not exist";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleLinkUpdateBadRequest(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(
            new ApiErrorResponse(
                BAD_REQUEST_DESCRIPTION,
                e.getStatusCode().toString(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ),
            e.getStatusCode() //400 Bad Request
        );
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<?> handleChatNotFound(ChatNotFoundException e) {
        return new ResponseEntity<>(
            new ApiErrorResponse(
                NOT_FOUND_DESCRIPTION,
                HttpStatus.NOT_FOUND.toString(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ),
            HttpStatus.NOT_FOUND
        );
    }

}
