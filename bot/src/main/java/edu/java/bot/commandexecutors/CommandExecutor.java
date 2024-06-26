package edu.java.bot.commandexecutors;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.responseeditorservices.ResponseService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandExecutor implements UpdatesListener {

    private final ResponseService responseService;
    private final TelegramBot bot;

    @Autowired
    public CommandExecutor(ResponseService responseService, TelegramBot bot) {
        this.responseService = responseService;
        this.bot = bot;
        bot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> list) {
        list.forEach(update -> {
            SendMessage message =
                responseService
                    .prepareResponse(update)
                    .disableWebPagePreview(true);
            bot.execute(message);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
