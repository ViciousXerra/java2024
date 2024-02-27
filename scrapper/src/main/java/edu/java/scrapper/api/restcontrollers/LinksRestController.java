package edu.java.scrapper.api.restcontrollers;

import edu.java.scrapper.api.dto.requests.AddLinkRequest;
import edu.java.scrapper.api.dto.requests.RemoveLinkRequest;
import edu.java.scrapper.api.dto.responses.LinkResponse;
import edu.java.scrapper.api.dto.responses.ListLinkResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scrapper/links")
public class LinksRestController {

    @GetMapping(produces = "application/json")
    public ResponseEntity<ListLinkResponse> getAllLinks(@RequestHeader("Tg-Chat-Id") long id) {
        /*
        TODO
        Get all links
         */
        return new ResponseEntity<>(
            new ListLinkResponse(
                List.of(
                    new LinkResponse(1L, "https://github.com/ViciousXerra"),
                    new LinkResponse(1L, "https://stackoverflow.com")
                ),
                2
            ),
            HttpStatus.OK
        );
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<LinkResponse> addLink(
        @RequestHeader("Tg-Chat-Id") long id,
        @Valid @RequestBody AddLinkRequest addLinkRequest
    ) {
        /*
        TODO
        Process addLinkRequest with given id
         */
        return new ResponseEntity<>(new LinkResponse(1L, "https://github.com/ViciousXerra"), HttpStatus.OK);
    }

    @DeleteMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<LinkResponse> deleteLink(
        @RequestHeader("Tg-Chat-Id") long id,
        @Valid @RequestBody RemoveLinkRequest removeLinkRequest
    ) {
        /*
        TODO
        Possible:
        throw new NotFoundException("Link doesn't exist",
            "Link %s associated with chat id %d haven't been founded".formatted(removeLinkRequest.link(), id)
        );
        Process removeLinkRequest with given id
         */
        return new ResponseEntity<>(new LinkResponse(1L, "https://github.com/ViciousXerra"), HttpStatus.OK);
    }

}
