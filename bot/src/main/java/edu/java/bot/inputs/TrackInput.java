package edu.java.bot.inputs;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.temp_repository.UserTemporaryRepository;
import edu.java.bot.users.Action;
import edu.java.bot.users.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component("/track")
@Qualifier("slash_input")
public class TrackInput implements Input {

    public static final String SPECIFY_LINK_MESSAGE =
        "Please, specify the link you are interested in by the next message.";
    public static final String UNREGISTERED_USER_MESSAGE =
        "First register in our service using the \"/start\" message.";
    private final UserTemporaryRepository repository;

    public TrackInput(UserTemporaryRepository repository) {
        this.repository = repository;
    }

    @Override
    public String text() {
        return "/track";
    }

    @Override
    public String description() {
        return "Allows you to specify the link you want to track in your next message.";
    }

    @Override
    public String prepareResponse(Update update) {
        Optional<User> user = getUserByChatId(update, repository);
        if (user.isEmpty()) {
            return UNREGISTERED_USER_MESSAGE;
        }
        user.get().setAction(Action.AWAITING_LINK_TO_TRACK);
        return SPECIFY_LINK_MESSAGE;
    }

}
