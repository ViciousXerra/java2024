package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.dispatch.InputsDispatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@DependsOn({"ApplicationConfig", "InputsConfig"})
public class BotConfig {

    @Bean
    TelegramBot telegramBot(ApplicationConfig applicationConfig, InputsDispatcher dispatcher) {
        TelegramBot bot = new TelegramBot(applicationConfig.telegramToken());
        bot.execute(initializeMenuButtons(dispatcher));
        return bot;
    }

    private SetMyCommands initializeMenuButtons(InputsDispatcher dispatcher) {
        return new SetMyCommands(
            dispatcher.getInputsMap().values()
                .stream()
                .map(input -> new BotCommand(input.text(), input.description()))
                .toArray(BotCommand[]::new)
        );
    }

}
