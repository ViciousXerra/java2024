package edu.java.bot.inputs;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.temp_repository.UserTemporaryRepository;
import edu.java.bot.users.User;
import java.net.URI;
import java.util.List;
import java.util.Optional;

public class ListInput implements Input {

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String UNREGISTERED_USER = "Please, register to use our service.";
    private static final String TRACKING_LINKS_EMPTY_LIST = "No links are being tracked at this time.";
    private static final String TRACKING_LINKS_HEADER = "Currently tracking links:";

    private final UserTemporaryRepository repository;

    public ListInput(UserTemporaryRepository repository) {
        this.repository = repository;
    }

    @Override
    public String text() {
        return "/list";
    }

    @Override
    public String description() {
        return "Allows you to show all currently tracked links.";
    }

    @Override
    public String prepareResponse(Update update) {
        Optional<User> user = getUserByChatId(update, repository);
        if (user.isEmpty()) {
            return UNREGISTERED_USER;
        }
        List<URI> userLinks = user.get().getLinks();
        if (userLinks.isEmpty()) {
            return TRACKING_LINKS_EMPTY_LIST;
        }
        StringBuilder sb = new StringBuilder();
        sb
            .append(TRACKING_LINKS_HEADER)
            .append(LINE_SEPARATOR);
        userLinks.forEach(link ->
            sb
                .append(link.toString())
                .append(LINE_SEPARATOR)
        );
        return sb.toString();
    }

}
