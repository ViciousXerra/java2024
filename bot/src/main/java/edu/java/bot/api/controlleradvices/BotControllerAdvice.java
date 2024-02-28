package edu.java.bot.api.controlleradvices;

import edu.java.bot.api.dto.errorresponses.ApiErrorResponse;
import edu.java.bot.api.exceptions.ConflictException;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BotControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiErrorResponse> handleLinkUpdateBadRequest(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(
            createApiErrorResponse("Invalid or incorrect request parameters", HttpStatus.BAD_REQUEST, e),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiErrorResponse> handleLinkUpdateConflict(ConflictException e) {
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
