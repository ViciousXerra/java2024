package edu.java.bot.inputs;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.temp_repository.UserTemporaryRepository;
import edu.java.bot.users.User;
import java.util.Optional;

public interface Input {

    String text();

    String description();

    String prepareResponse(Update update);

    default Optional<User> getUserByChatId(Update update, UserTemporaryRepository repository) {
        return repository.getPossibleUser(update.message().chat().id());
    }

    default String getUserName(Update update) {
        return update.message().chat().username();
    }

}
