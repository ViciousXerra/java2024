package edu.java.bot.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;

public interface Command {

    String command();

    String description();

    String createMessage(String text, String username, long id);

    default boolean isSupport(Update update) {
        return update.message().text().startsWith(command());
    }

    default BotCommand toBotCommand() {
        return new BotCommand(command(), description());
    }

}
