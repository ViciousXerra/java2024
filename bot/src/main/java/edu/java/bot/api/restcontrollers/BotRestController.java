package edu.java.bot.api.restcontrollers;

import edu.java.bot.api.dto.requests.LinkUpdate;
import edu.java.bot.commandexecutors.LinkUpdateCommandExecutor;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot/updates")
public class BotRestController {

    private final LinkUpdateCommandExecutor linkUpdateCommandExecutor;

    @Autowired
    public BotRestController(LinkUpdateCommandExecutor linkUpdateCommandExecutor) {
        this.linkUpdateCommandExecutor = linkUpdateCommandExecutor;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> postLinkUpdate(@Valid @RequestBody LinkUpdate linkUpdate) {
        linkUpdateCommandExecutor.process(linkUpdate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
