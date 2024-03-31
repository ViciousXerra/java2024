package edu.java.scrapper.api.controlleradvices;

import edu.java.scrapper.api.dto.errorresponses.ApiErrorResponse;
import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.api.exceptions.RateLimitException;
import edu.java.scrapper.api.exceptions.UnhandledException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ScrapperControllerAdvice {

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class,
        MissingRequestHeaderException.class})
    public ResponseEntity<ApiErrorResponse> handleLinkUpdateBadRequest(Exception e) {
        return new ResponseEntity<>(
            createApiErrorResponse("Invalid or incorrect request parameters", HttpStatus.BAD_REQUEST, e),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException e) {
        return new ResponseEntity<>(
            createApiErrorResponse(e.getDescription(), HttpStatus.NOT_FOUND, e),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException e) {
        return new ResponseEntity<>(
            createApiErrorResponse(e.getDescription(), HttpStatus.CONFLICT, e),
            HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(UnhandledException.class)
    public ResponseEntity<ApiErrorResponse> handleLinkUpdateBadRequest(UnhandledException e) {
        return new ResponseEntity<>(
            createApiErrorResponse("Unable to correctly satisfy a request", HttpStatus.INTERNAL_SERVER_ERROR, e),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ApiErrorResponse> handleRateLimit(RateLimitException e) {
        return new ResponseEntity<>(
            createApiErrorResponse(e.getDescription(), HttpStatus.TOO_MANY_REQUESTS, e),
            HttpStatus.TOO_MANY_REQUESTS
        );
    }

    private static ApiErrorResponse createApiErrorResponse(String description, HttpStatus httpStatus, Exception e) {
        return new ApiErrorResponse(
            description,
            String.valueOf(httpStatus.value()),
            e.getClass().getSimpleName(),
            e.getMessage(),
            Arrays.stream(e.getStackTrace())
                .map(stackTraceElement -> "%s: %s".formatted(
                        stackTraceElement.getClassName(),
                        stackTraceElement.getMethodName()
                    )
                )
                .toList()
        );
    }

}
