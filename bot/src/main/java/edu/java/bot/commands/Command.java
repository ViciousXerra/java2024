package edu.java.bot.commands;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.users.User;
import java.util.Optional;

public interface Command {

    String command();

    String description();

    String createMessage(Optional<User> optionalUser, String username, long id);

    default boolean supports(Update update) {
        return command().equals(update.message().text());
    }

    default BotCommand toBotCommand() {
        return new BotCommand(command(), description());
    }

}
