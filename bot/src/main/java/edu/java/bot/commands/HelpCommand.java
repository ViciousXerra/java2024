package edu.java.bot.commands;

import edu.java.bot.users.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String BOT_PURPOSE =
        "This bot allows you to track links and sends responses when the resource is updated.";
    private static final String TEXT_AND_DESCRIPTION_TEMPLATE = "%s: %s";
    private final List<Command> allSupportedCommands;

    public HelpCommand(List<Command> allSupportedCommands) {
        this.allSupportedCommands = allSupportedCommands;
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return "Shows a list of all commands.";
    }

    @Override
    public String createMessage(Optional<User> optionalUser, String username, long id) {
        StringBuilder sb = new StringBuilder();
        sb
            .append(BOT_PURPOSE)
            .append(LINE_SEPARATOR);
        allSupportedCommands.forEach(
            command ->
                sb
                    .append(TEXT_AND_DESCRIPTION_TEMPLATE.formatted(command.command(), command.description()))
                    .append(LINE_SEPARATOR)
        );
        return sb.toString();
    }

}
