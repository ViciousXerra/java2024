package edu.java.bot.commandtests;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.applisteners.BotInitializationListener;
import edu.java.bot.commands.Command;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import java.util.List;

@SpringBootTest
abstract class CommandTest {

    @MockBean
    private TelegramBot bot;

    @TestConfiguration
    static class TestingConfig {

        @Bean
        ApplicationListener<ContextRefreshedEvent> otInitializationListener(
            TelegramBot bot,
            List<Command> allSupportedCommands
        ) {
            return new BotInitializationListener(bot, allSupportedCommands);
        }

    }

}
