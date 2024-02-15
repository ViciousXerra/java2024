package edu.java.bot.commands;

import edu.java.bot.users.User;
import edu.java.bot.users.UserChatCondition;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand implements Command {

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Allows you to remove interesting links by next message.";
    }

    @Override
    public String createMessage(Optional<User> optionalUser, String username, long id) {
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            UserChatCondition condition = user.getCondition();
            if (condition.equals(UserChatCondition.DEFAULT)) {
                user.setCondition(UserChatCondition.AWAITING_LINK_TO_UNTRACK);
                return "Waiting for a link to be entered.";
            } else {
                return "The link to save/delete is already expected.";
            }
        }
        return "First you need to register by entering the command /start.";
    }

}
