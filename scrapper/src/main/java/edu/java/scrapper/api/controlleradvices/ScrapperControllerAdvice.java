package edu.java.scrapper.api.controlleradvices;

import edu.java.scrapper.api.dto.errorresponses.ApiErrorResponse;
import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ScrapperControllerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingRequestHeaderException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleLinkUpdateBadRequest(Exception e) {
        return new ResponseEntity<>(
            createApiErrorResponse("Invalid or incorrect request parameters", HttpStatus.BAD_REQUEST, e),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException e) {
        return new ResponseEntity<>(
            createApiErrorResponse(e.getDescription(), HttpStatus.NOT_FOUND, e),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException e) {
        return new ResponseEntity<>(
            createApiErrorResponse(e.getDescription(), HttpStatus.CONFLICT, e),
            HttpStatus.CONFLICT
        );
    }

    private static ApiErrorResponse createApiErrorResponse(String description, HttpStatus httpStatus, Exception e) {
        return new ApiErrorResponse(
            description,
            httpStatus.toString(),
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
