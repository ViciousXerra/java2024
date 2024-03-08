package edu.java.bot.api.restcontrollers;

import edu.java.bot.api.dto.errorresponses.ApiErrorResponse;
import edu.java.bot.api.dto.requests.LinkUpdate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot/updates")
public class BotRestController {

    @Operation(summary = "Post a link update")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Link update was successfully processed"),
        @ApiResponse(
            responseCode = "400", description = "Invalid request body",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409", description = "Link update already exists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> postLinkUpdate(@Valid @RequestBody LinkUpdate linkUpdate) {
        /*
        TODO
        Possible: throw new ConflictException
        Post linkUpdate
         */
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
