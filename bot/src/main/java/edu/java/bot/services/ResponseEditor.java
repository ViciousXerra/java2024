package edu.java.bot.services;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.temprepo.Registry;
import edu.java.bot.urlparsers.AbstractUrlParser;
import edu.java.bot.users.User;
import edu.java.bot.users.UserChatCondition;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResponseEditor implements ResponseService {

    private final List<Command> allSupportedCommands;
    private final AbstractUrlParser urlParser;
    private final Registry repository;

    @Autowired
    public ResponseEditor(List<Command> allSupportedCommands, AbstractUrlParser urlParser, Registry repository) {
        this.allSupportedCommands = allSupportedCommands;
        this.urlParser = urlParser;
        this.repository = repository;
    }

    @Override
    public SendMessage prepareResponse(Update update) {
        long id = update.message().chat().id();
        Optional<User> optionalUser = repository.getById(id);
        String username = update.message().chat().username();
        String incomingText = update.message().text();
        List<Command> commandMatch = allSupportedCommands.stream().filter(command -> command.supports(update)).toList();
        if (commandMatch.size() > 1) {
            return new SendMessage(id, "Invalid service state.");
        } else if (commandMatch.size() == 1) {
            return new SendMessage(
                id,
                commandMatch.getFirst().createMessage(optionalUser, username, id)
            );
        } else {
            return new SendMessage(id, processUrl(optionalUser, incomingText));
        }
    }

    private String processUrl(Optional<User> optionalUser, String text) {
        if (optionalUser.isPresent()) {
            if (urlParser.isValid(text)) {
                User user = optionalUser.get();
                URI url = URI.create(text);
                return manageUrlAndCreateNotificationMessage(user, url);
            } else {
                return "The link is incorrectly formatted or this resource is not supported.";
            }
        }
        return "First you need to register by entering the command /start.";
    }

    private String manageUrlAndCreateNotificationMessage(User user, URI url) {
        UserChatCondition condition = user.getCondition();
        if (condition.equals(UserChatCondition.AWAITING_LINK_TO_TRACK)) {
            user.saveLink(url);
            user.setCondition(UserChatCondition.DEFAULT);
            return "The link was successfully saved.";
        } else if (condition.equals(UserChatCondition.AWAITING_LINK_TO_UNTRACK)) {
            user.removeLink(url);
            user.setCondition(UserChatCondition.DEFAULT);
            return "The link was successfully deleted.";
        } else {
            return "Invalid state.";
        }
    }

}
