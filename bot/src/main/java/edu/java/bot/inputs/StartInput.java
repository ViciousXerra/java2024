package edu.java.bot.inputs;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.temp_repository.UserTemporaryRepository;
import edu.java.bot.users.Action;
import edu.java.bot.users.User;
import java.util.ArrayList;
import java.util.Optional;

public class StartInput implements Input {

    private static final String SUCCESSFULLY_REGISTERED_MESSAGE = "%s, you have been successfully registered. Hello!";
    private static final String ALREADY_REGISTERED_MESSAGE = "%s, my friend! What's up?";

    private final UserTemporaryRepository repository;

    public StartInput(UserTemporaryRepository repository) {
        this.repository = repository;
    }

    @Override
    public String text() {
        return "/start";
    }

    @Override
    public String description() {
        return "Allows you to register or start using this service.";
    }

    @Override
    public String prepareResponse(Update update) {
        String userName = getUserName(update);
        Optional<User> user = getUserByChatId(update, repository);
        if (user.isEmpty()) {
            repository.save(new User(update.message().chat().id(), new ArrayList<>(), Action.DEFAULT));
            return String.format(SUCCESSFULLY_REGISTERED_MESSAGE, userName);
        }
        return String.format(ALREADY_REGISTERED_MESSAGE, userName);
    }

}
