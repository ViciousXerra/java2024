package edu.java.bot.scrapperclient.clients;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "/tg-chat")
public interface ChatClient {

    @PostExchange(url = "/{id}")
    ResponseEntity<?> signUpChat(@PathVariable long id);

    @DeleteExchange(url = "/{id}")
    ResponseEntity<?> removeChat(@PathVariable long id);

}
