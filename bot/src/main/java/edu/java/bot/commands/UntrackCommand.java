package edu.java.bot.commands;

import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperservices.ScrapperService;
import edu.java.bot.urlparsers.AbstractUrlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand implements Command {

    private static final String VALID_SYNTAX_PATTERN = "^/untrack .+$";
    private static final int LINK_START_CHAR_INDEX = 9;
    private final AbstractUrlParser urlParser;
    private final ScrapperService scrapperService;

    @Autowired
    public UntrackCommand(AbstractUrlParser urlParser, ScrapperService scrapperService) {
        this.urlParser = urlParser;
        this.scrapperService = scrapperService;
    }

    @Override
    public String command() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Allows you to remove interesting links by passing link after whitespace.";
    }

    @Override
    public String createMessage(String text, String username, long id) {
        boolean syntaxMatches = text.matches(VALID_SYNTAX_PATTERN);
        if (syntaxMatches && urlParser.isValid(text.substring(LINK_START_CHAR_INDEX))) {
            try {
                scrapperService.removeLink(id, text.substring(LINK_START_CHAR_INDEX));
                return "Deleted.";
            } catch (ClientException e) {
                return e.getClientErrorResponseBody().description();
            }
        } else if (syntaxMatches) {
            return "The link does not satisfy the URI pattern requirements or the given resource is not supported.";
        } else {
            return "Please, pass link after \"/untrack\" command. Command and link must be delimited with whitespace.";
        }
    }

}
