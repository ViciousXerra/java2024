package edu.java.bot.commandexecutor;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.services.ResponseService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CommandExecutor implements UpdatesListener {

    private final ResponseService responseService;
    private final TelegramBot bot;

    @Autowired
    public CommandExecutor(ResponseService responseService, @Qualifier("main") TelegramBot bot) {
        this.responseService = responseService;
        this.bot = bot;
        bot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> list) {
        list.forEach(update -> bot.execute(responseService.prepareResponse(update)));
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
