package edu.java.bot.commands;

import edu.java.bot.users.User;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ListCommand implements Command {

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "Shows a list of all tracking links.";
    }

    @Override
    public String createMessage(Optional<User> optionalUser, String username, long id) {
        if (optionalUser.isPresent()) {
            Set<URI> links = optionalUser.get().getTrackingLinks();
            if (links.isEmpty()) {
                return "You are not tracking any links.";
            }
            StringBuilder sb = new StringBuilder();
            links.forEach(
                link ->
                    sb
                        .append(link)
                        .append(System.lineSeparator())
            );
            return sb.toString();
        }
        return "First you need to register by entering the command /start.";
    }

}
