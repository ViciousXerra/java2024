package edu.java.bot.commands;

import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperservices.ScrapperService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Log4j2
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
        } catch (WebClientResponseException e) {
            log.error("Service unavailable: {}", e.getMessage());
            return "Unavailable to reach service.";
        }
    }

}
