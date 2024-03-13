package edu.java.scrapper.api.restcontrollers;

import edu.java.scrapper.api.dto.requests.AddLinkRequest;
import edu.java.scrapper.api.dto.requests.RemoveLinkRequest;
import edu.java.scrapper.api.dto.responses.LinkResponse;
import edu.java.scrapper.api.dto.responses.ListLinkResponse;
import jakarta.validation.Valid;
import java.net.URI;
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

    private final static URI LINK_1 = URI.create("https://github.com/ViciousXerra");
    private final static URI LINK_2 = URI.create("https://stackoverflow.com");

    @GetMapping(produces = "application/json")
    public ResponseEntity<ListLinkResponse> getAllLinks(@RequestHeader("Tg-Chat-Id") long id) {
        /*
        TODO
        Possible: throw new NotFoundException
        Get all links associated with given id
         */
        return new ResponseEntity<>(
            new ListLinkResponse(
                List.of(
                    new LinkResponse(1L, LINK_1),
                    new LinkResponse(1L, LINK_2)
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
        Possible: throw new NotFoundException
        Process addLinkRequest associated with given id
         */
        return new ResponseEntity<>(new LinkResponse(1L, LINK_2), HttpStatus.OK);
    }

    @DeleteMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<LinkResponse> deleteLink(
        @RequestHeader("Tg-Chat-Id") long id,
        @Valid @RequestBody RemoveLinkRequest removeLinkRequest
    ) {
        /*
        TODO
        Possible: throw new NotFoundException
        Process removeLinkRequest associated with given id
         */
        return new ResponseEntity<>(new LinkResponse(1L, LINK_1), HttpStatus.OK);
    }

}
