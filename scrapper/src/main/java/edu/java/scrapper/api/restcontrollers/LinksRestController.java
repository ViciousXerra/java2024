package edu.java.scrapper.api.restcontrollers;

import edu.java.scrapper.api.dto.requests.AddLinkRequest;
import edu.java.scrapper.api.dto.requests.RemoveLinkRequest;
import edu.java.scrapper.api.dto.responses.LinkResponse;
import edu.java.scrapper.api.dto.responses.ListLinkResponse;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static java.net.URI.create;

@RestController
@RequestMapping("/scrapper/links")
public class LinksRestController {

    private final static String HEADER_LABEL = "Tg-Chat-Id";
    private final LinkService linkService;

    @Autowired
    public LinksRestController(@Qualifier("jdbc-link-service") LinkService linkService) {
        this.linkService = linkService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<ListLinkResponse> getAllLinks(@RequestHeader(HEADER_LABEL) long id) {
        Collection<Link> links = linkService.listAll(id);
        List<LinkResponse> linkResponses =
            links.stream().map(link -> new LinkResponse(link.linkId(), create(link.url()))).toList();
        return new ResponseEntity<>(
            new ListLinkResponse(
                linkResponses,
                linkResponses.size()
            ),
            HttpStatus.OK
        );
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<LinkResponse> addLink(
        @RequestHeader(HEADER_LABEL) long id,
        @Valid @RequestBody AddLinkRequest addLinkRequest
    ) {
        Link link = linkService.add(id, addLinkRequest.link());
        return new ResponseEntity<>(new LinkResponse(link.linkId(), create(link.url())), HttpStatus.OK);
    }

    @DeleteMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<LinkResponse> deleteLink(
        @RequestHeader(HEADER_LABEL) long id,
        @Valid @RequestBody RemoveLinkRequest removeLinkRequest
    ) {
        Link link = linkService.remove(id, removeLinkRequest.link());
        return new ResponseEntity<>(new LinkResponse(link.linkId(), create(link.url())), HttpStatus.OK);
    }

}
