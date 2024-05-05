package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.applisteners.BotInitializationListener;
import edu.java.bot.commands.Command;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import java.util.List;

@TestConfiguration
public class BaseTestConfig {

    @Bean
    public ApplicationListener<ContextRefreshedEvent> botInitializationListener(
        TelegramBot bot,
        List<Command> allSupportedCommands
    ) {
        return new BotInitializationListener(bot, allSupportedCommands);
    }

}
