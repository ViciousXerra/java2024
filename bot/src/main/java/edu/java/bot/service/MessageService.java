package edu.java.bot.service;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.inputs.Input;
import edu.java.bot.parsers.URLParser;
import edu.java.bot.temp_repository.UserTemporaryRepository;
import edu.java.bot.users.Action;
import edu.java.bot.users.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private static final String REGISTRATION_REQUIRED_MESSAGE = "Registration required.";
    private static final String INVALID_URI_SOURCE_MESSAGE = "Invalid URI source.";
    private static final String INVALID_INPUT_STATE_MESSAGE = "Invalid state to this input.";
    private static final String SUCCESS_TRACK_MESSAGE = "Link tracking has started.";
    private static final String ALREADY_TRACK_MESSAGE = "Already tracking this link.";
    private static final String UNVERIFIED_HOSTNAME_TO_TRACK =
        "Links with this hostname are not supported.";
    private static final String SUCCESS_UNTRACK_MESSAGE = "Link tracking has stopped.";
    private static final String ALREADY_UNTRACK_MESSAGE = "This link is not tracked.";

    private final Map<String, Input> inputMap;
    private final UserTemporaryRepository repository;
    private final URLParser urlParser;

    public MessageService(
        Map<String, Input> inputMap,
        UserTemporaryRepository repository,
        URLParser urlParser
    ) {
        this.inputMap = inputMap;
        this.repository = repository;
        this.urlParser = urlParser;
    }

    public String prepareResponse(Update update) {
        long chatId = update.message().chat().id();
        String textMessage = update.message().text();
        Input input = inputMap.get(textMessage);
        return (input != null) ? input.prepareResponse(update) :
            processNonSlashInput(chatId, textMessage);
    }

    private String processNonSlashInput(Long chatId, String text) {
        Optional<User> userOptional = repository.getPossibleUser(chatId);
        if (userOptional.isEmpty()) {
            return REGISTRATION_REQUIRED_MESSAGE;
        }
        User user = userOptional.get();
        try {
            URI url = new URI(text);
            return processAwaitingStateUserInput(user, url);
        } catch (URISyntaxException e) {
            return INVALID_URI_SOURCE_MESSAGE;
        }
    }

    private String processAwaitingStateUserInput(User user, URI uri) {
        if (user.getAction().equals(Action.AWAITING_LINK_TO_TRACK)) {
            return prepareAwaitingLinkToTrackResponse(user, uri);
        }
        if (user.getAction().equals(Action.AWAITING_LINK_TO_UNTRACK)) {
            return prepareAwaitingLinkToUntrackResponse(user, uri);
        }
        return INVALID_INPUT_STATE_MESSAGE;
    }

    private String prepareAwaitingLinkToTrackResponse(User user, URI url) {
        if (urlParser.isVerifiedHostUrl(url)) {
            return (addUserLink(user, url)) ? SUCCESS_TRACK_MESSAGE : ALREADY_TRACK_MESSAGE;
        }
        return UNVERIFIED_HOSTNAME_TO_TRACK;
    }

    private String prepareAwaitingLinkToUntrackResponse(User user, URI url) {
        if (urlParser.isVerifiedHostUrl(url)) {
            return (deleteUserLink(user, url)) ? SUCCESS_UNTRACK_MESSAGE : ALREADY_UNTRACK_MESSAGE;
        }
        return UNVERIFIED_HOSTNAME_TO_TRACK;
    }

    private boolean addUserLink(User user, URI uri) {
        List<URI> links = new ArrayList<>(user.getLinks());
        if (links.contains(uri)) {
            return false;
        }
        links.add(uri);
        resetUserAction(user);
        return true;
    }

    private boolean deleteUserLink(User user, URI uri) {
        List<URI> links = new ArrayList<>(user.getLinks());
        if (!links.contains(uri)) {
            return false;
        }
        links.remove(uri);
        resetUserAction(user);
        return true;
    }

    private void resetUserAction(User user) {
        user.setAction(Action.DEFAULT);
    }

}
