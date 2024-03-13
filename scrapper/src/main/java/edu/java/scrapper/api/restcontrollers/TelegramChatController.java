package edu.java.scrapper.api.restcontrollers;

import edu.java.scrapper.api.dto.errorresponses.ApiErrorResponse;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scrapper/tg-chat")
public class TelegramChatController {

    private final ChatService chatService;

    @Autowired
    public TelegramChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "Sign up telegram chat")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chat has been successfully signed up"),
        @ApiResponse(
            responseCode = "400", description = "Invalid request parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409", description = "Chat has been already signed up",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    @PostMapping("/{id}")
    public ResponseEntity<?> chatSignUp(@PathVariable long id) {
        chatService.register(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Delete telegram chat")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chat has been successfully deleted"),
        @ApiResponse(
            responseCode = "400", description = "Invalid request parameters",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", description = "Chat does not exist",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChat(@PathVariable long id) {
        chatService.unregister(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
