package edu.java.bot.commands;

import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperservices.ScrapperService;
import java.net.URI;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Log4j2
public class ListCommand implements Command {

    private static final String LINK_TEMPLATE = "link %d: %s";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private final ScrapperService scrapperService;

    @Autowired
    public ListCommand(ScrapperService scrapperService) {
        this.scrapperService = scrapperService;
    }

    @Override
    public String command() {
        return "/list";
    }

    @Override
    public String description() {
        return "Shows a list of all tracking links.";
    }

    @Override
    public String createMessage(String text, String username, long id) {
        try {
            List<URI> linksList = scrapperService.getAllLinks(id);
            if (linksList.isEmpty()) {
                return "You are not tracking any links.";
            }
            int counter = 1;
            StringBuilder builder = new StringBuilder();
            for (URI link : linksList) {
                builder.append(LINK_TEMPLATE.formatted(counter++, link.toString())).append(LINE_SEPARATOR);
            }
            return builder.toString();
        } catch (ClientException e) {
            return e.getClientErrorResponseBody().description();
        } catch (WebClientResponseException e) {
            log.error("Service unavailable: {}", e.getMessage());
            return "Unavailable to reach service.";
        }
    }

}
