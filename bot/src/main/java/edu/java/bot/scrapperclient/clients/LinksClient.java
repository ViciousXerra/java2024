package edu.java.bot.scrapperclient.clients;

import edu.java.bot.scrapperclient.dto.requests.AddLinkRequest;
import edu.java.bot.scrapperclient.dto.requests.RemoveLinkRequest;
import edu.java.bot.scrapperclient.dto.responses.LinkResponse;
import edu.java.bot.scrapperclient.dto.responses.ListLinkResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "/links")
public interface LinksClient {

    String HEADER_LABEL = "Tg-Chat-Id";

    @GetExchange
    ListLinkResponse getAllLinks(@RequestHeader(HEADER_LABEL) long chatId);

    @PostExchange
    LinkResponse addLink(
        @RequestHeader(HEADER_LABEL) long chatId,
        @RequestBody AddLinkRequest addLinkRequest
    );

    @DeleteExchange
    LinkResponse removeLink(
        @RequestHeader(HEADER_LABEL) long chatId,
        @RequestBody RemoveLinkRequest removeLinkRequest
    );

}
