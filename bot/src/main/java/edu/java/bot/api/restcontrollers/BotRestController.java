package edu.java.bot.api.restcontrollers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.dto.requests.LinkUpdate;
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

    private final TelegramBot telegramBot;

    @Autowired
    public BotRestController(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> postLinkUpdate(@Valid @RequestBody LinkUpdate linkUpdate) {
        linkUpdate.tgChatIds().stream()
            .map(chatId -> new SendMessage(chatId, linkUpdate.description()).disableWebPagePreview(true))
            .forEach(telegramBot::execute);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
