package edu.java.bot.applisteners;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.Command;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class BotInitializationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final TelegramBot bot;
    private final List<Command> allSupportedCommands;

    @Autowired
    public BotInitializationListener(@Qualifier("main") TelegramBot bot, List<Command> allSupportedCommands) {
        this.bot = bot;
        this.allSupportedCommands = allSupportedCommands;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        bot.execute(new SetMyCommands(allSupportedCommands.stream().map(Command::toBotCommand)
            .toArray(BotCommand[]::new)));
    }

}
