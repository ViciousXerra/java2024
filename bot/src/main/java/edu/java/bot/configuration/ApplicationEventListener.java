package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.inputs.Input;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    private TelegramBot bot;
    @Autowired
    private Map<String, Input> inputMap;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            bot.execute(initializeMenuButtons(inputMap));
        }
    }

    private SetMyCommands initializeMenuButtons(Map<String, Input> inputMap) {
        return new SetMyCommands(
            inputMap.values()
                .stream()
                .map(input -> new BotCommand(input.text(), input.description()))
                .toArray(BotCommand[]::new)
        );
    }

}
