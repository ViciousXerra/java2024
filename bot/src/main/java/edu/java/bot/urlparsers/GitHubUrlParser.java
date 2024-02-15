package edu.java.bot.urlparsers;

import java.util.regex.Matcher;

public class GitHubUrlParser extends AbstractUrlParser {

    private static final String HOSTNAME = "github.com";

    public GitHubUrlParser(AbstractUrlParser nextParser) {
        super(nextParser);
    }

    @Override
    protected boolean isVerifiedHost(Matcher matcher) {
        return HOSTNAME.equals(matcher.group(HOSTNAME_MATCH_GROUP));
    }

}
