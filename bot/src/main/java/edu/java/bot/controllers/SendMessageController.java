package edu.java.bot.controllers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import edu.java.bot.service.MessageService;
import java.util.List;
import org.springframework.stereotype.Controller;

@Controller
public class SendMessageController implements UpdatesListener {

    private final TelegramBot bot;
    private final MessageService messageService;

    public SendMessageController(TelegramBot bot, MessageService messageService) {
        bot.setUpdatesListener(this);
        this.bot = bot;
        this.messageService = messageService;
    }

    @Override
    public int process(List<Update> list) {
        if (!list.isEmpty()) {
            list.forEach(update -> {
                if (update != null) {
                    SendMessage request = new SendMessage(
                        update.message().chat().id(),
                        messageService.prepareResponse(update)
                    )
                        .parseMode(ParseMode.MarkdownV2)
                        .disableWebPagePreview(true)
                        .replyToMessageId(1);
                    SendResponse sendResponse = bot.execute(request);
                    boolean ok = sendResponse.isOk();
                    Message message = sendResponse.message();
                }
            });
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
