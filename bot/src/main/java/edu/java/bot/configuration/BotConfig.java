package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.commands.Command;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Bean
    TelegramBot telegramBot(ApplicationConfig config, List<Command> allSupportedCommands) {
        TelegramBot bot = new TelegramBot(config.telegramToken());
        bot.execute(new SetMyCommands(allSupportedCommands.stream().map(Command::toBotCommand)
            .toArray(BotCommand[]::new)));
        return bot;
    }

}
