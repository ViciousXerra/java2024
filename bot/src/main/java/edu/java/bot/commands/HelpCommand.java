package edu.java.bot.commands;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String BOT_PURPOSE =
        "This bot allows you to track links and sends responses when the resource is updated."
        + LINE_SEPARATOR
        + "Currently supports:"
        + LINE_SEPARATOR
        + "GitHub repositories updates;"
        + LINE_SEPARATOR
        + "StackOverFlow questions updates;"
        + LINE_SEPARATOR;
    private static final String TEXT_AND_DESCRIPTION_TEMPLATE = "%s: %s";
    private final List<Command> allSupportedCommands;

    @Autowired
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
    public String createMessage(String text, String username, long id) {
        StringBuilder sb = new StringBuilder();
        sb.append(BOT_PURPOSE).append(LINE_SEPARATOR);
        for (Command command : allSupportedCommands) {
            sb.append(TEXT_AND_DESCRIPTION_TEMPLATE.formatted(command.command(), command.description()))
                .append(LINE_SEPARATOR);
        }
        return sb.toString();
    }

}
