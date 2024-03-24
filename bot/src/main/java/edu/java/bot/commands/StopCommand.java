package edu.java.bot.commands;

import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperservices.ScrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopCommand implements Command {

    private static final String UNSUB_USER_MESSAGE_TEMPLATE = "Thank you for using this service, %s.";
    private final ScrapperService scrapperService;

    @Autowired
    public StopCommand(ScrapperService scrapperService) {
        this.scrapperService = scrapperService;
    }

    @Override
    public String command() {
        return "/stop";
    }

    @Override
    public String description() {
        return "Allows you to stop using the service and unsubscribe from link updates.";
    }

    @Override
    public String createMessage(String text, String username, long id) {
        try {
            scrapperService.removeChat(id);
            return UNSUB_USER_MESSAGE_TEMPLATE.formatted(username);
        } catch (ClientException e) {
            return e.getClientErrorResponseBody().description();
        }
    }

}
