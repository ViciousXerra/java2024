package edu.java.bot.commands;

import edu.java.bot.memoryuserrepository.UserRepository;
import edu.java.bot.users.User;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements Command {

    private static final String NEW_USER_GREETINGS_TEMPLATE = "Nice to meet you, %s.";
    private static final String ALREADY_REGISTERED_TEMPLATE = "Hello again, %s. Shall we continue?";
    private final UserRepository registry;

    @Autowired
    public StartCommand(UserRepository registry) {
        this.registry = registry;
    }

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Allows you to start using the service.";
    }

    @Override
    public String createMessage(Optional<User> optionalUser, String username, long id) {
        if (optionalUser.isPresent()) {
            return ALREADY_REGISTERED_TEMPLATE.formatted(username);
        } else {
            registry.saveInDb(id);
            return NEW_USER_GREETINGS_TEMPLATE.formatted(username);
        }
    }

}
