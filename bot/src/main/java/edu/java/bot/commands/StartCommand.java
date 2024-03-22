package edu.java.bot.commands;

import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperservices.ScrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements Command {

    private static final String NEW_USER_GREETINGS_TEMPLATE = "Nice to meet you, %s.";
    private final ScrapperService scrapperService;

    @Autowired
    public StartCommand(ScrapperService scrapperService) {
        this.scrapperService = scrapperService;
    }

    @Override
    public String command() {
        return "/start";
    }

    @Override
    public String description() {
        return "Allows you to start using the service and subscribe for link updates.";
    }

    @Override
    public String createMessage(String text, String username, long id) {
        try {
            scrapperService.addChat(id);
            return NEW_USER_GREETINGS_TEMPLATE.formatted(username);
        } catch (ClientException e) {
            return e.getClientErrorResponseBody().description();
        }
    }

}
