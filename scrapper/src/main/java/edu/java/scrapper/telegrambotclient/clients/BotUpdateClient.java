package edu.java.scrapper.telegrambotclient.clients;

import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "/updates")
public interface BotUpdateClient {

    @PostExchange
    ResponseEntity<?> postLinkUpdate(@RequestBody LinkUpdate linkUpdate);

}
