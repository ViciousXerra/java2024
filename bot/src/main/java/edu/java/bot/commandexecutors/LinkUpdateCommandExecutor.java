package edu.java.bot.commandexecutors;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.api.dto.requests.LinkUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LinkUpdateCommandExecutor {

    private final TelegramBot bot;

    @Autowired
    public LinkUpdateCommandExecutor(TelegramBot bot) {
        this.bot = bot;
    }

    public void process(LinkUpdate linkUpdate) {
        linkUpdate.tgChatIds().stream()
            .map(chatId -> new SendMessage(chatId, linkUpdate.description()).disableWebPagePreview(true))
            .forEach(bot::execute);
    }

}
