package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.inputs.Input;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;

@Configuration
public class BotConfig {

    @Bean
    TelegramBot telegramBot(ApplicationConfig applicationConfig) {
        TelegramBot bot = new TelegramBot(applicationConfig.telegramToken());
        return bot;
    }

}
