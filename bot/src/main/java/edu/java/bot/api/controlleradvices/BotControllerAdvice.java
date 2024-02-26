package edu.java.bot.api.controlleradvices;

import edu.java.bot.api.dto.errorresponses.ApiErrorResponse;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BotControllerAdvice {

    private final static String DESCRIPTION = "Invalid or incorrect request parameters";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleLinkUpdateBadRequest(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(
            new ApiErrorResponse(
                DESCRIPTION,
                e.getStatusCode().toString(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toList()
            ),
            e.getStatusCode() //400 Bad Request
        );
    }

}
