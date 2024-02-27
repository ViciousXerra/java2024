package edu.java.scrapper.api.controlleradvices;

import edu.java.scrapper.api.dto.errorresponses.ApiErrorResponse;
import edu.java.scrapper.api.exceptions.NotFoundException;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ScrapperControllerAdvice {

    private final static String BAD_REQUEST_DESCRIPTION = "Invalid or incorrect request parameters";

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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException e) {
        return new ResponseEntity<>(
            new ApiErrorResponse(
                e.getDescription(),
                e.getStatusCode().toString(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ),
            e.getStatusCode() //404 Not Found
        );
    }

}
