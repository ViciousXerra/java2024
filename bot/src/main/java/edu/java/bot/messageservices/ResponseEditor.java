package edu.java.bot.messageservices;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResponseEditor implements ResponseService {

    private final List<Command> allSupportedCommands;

    @Autowired
    public ResponseEditor(List<Command> allSupportedCommands) {
        this.allSupportedCommands = allSupportedCommands;
    }

    @Override
    public SendMessage prepareResponse(Update update) {
        long id = update.message().chat().id();
        String username = update.message().chat().username();
        String incomingText = update.message().text();
        List<Command> commandMatch =
            allSupportedCommands.stream().filter(command -> command.isSupport(update)).toList();
        if (commandMatch.size() == 1) {
            return new SendMessage(
                id,
                commandMatch.getFirst().createMessage(incomingText, username, id)
            );
        } else if (commandMatch.size() > 1) {
            return new SendMessage(id, "Invalid service state.");
        } else {
            return new SendMessage(id, "Unknown command.");
        }
    }

}
