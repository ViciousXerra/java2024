package edu.java.bot.urlparsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractUrlParser {

    private final AbstractUrlParser nextParser;
    private static final String LINK_REGEX =
        "https?://(www\\.)?([-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b)([-a-zA-Z0-9()@:%_+.~#?&/=]*)";
    private static final Pattern LINK_PATTERN = Pattern.compile(LINK_REGEX);
    protected static final int HOSTNAME_MATCH_GROUP = 2;

    public AbstractUrlParser(AbstractUrlParser nextParser) {
        this.nextParser = nextParser;
    }

    protected abstract boolean isVerifiedHost(Matcher matcher);

    public final boolean isValid(String link) {
        Matcher matcher = LINK_PATTERN.matcher(link);
        return matcher.matches()
               && (isVerifiedHost(matcher) || (nextParser != null && nextParser.isVerifiedHost(matcher)));
    }

}
