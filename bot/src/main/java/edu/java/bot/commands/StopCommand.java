package edu.java.bot.commands;

import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperservices.ScrapperService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Log4j2
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
        } catch (WebClientResponseException e) {
            log.error("Service unavailable: {}", e.getMessage());
            return "Unavailable to reach service.";
        }
    }

}
