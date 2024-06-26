package edu.java.bot.api.controlleradvices;

import edu.java.bot.api.dto.errorresponses.ApiErrorResponse;
import edu.java.bot.api.exceptions.RateLimitException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BotControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleLinkUpdateBadRequest(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(
            createApiErrorResponse("Invalid or incorrect request parameters", HttpStatus.BAD_REQUEST, e),
            HttpStatus.BAD_REQUEST
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
