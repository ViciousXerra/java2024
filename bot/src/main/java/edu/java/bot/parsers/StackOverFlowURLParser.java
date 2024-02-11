package edu.java.bot.parsers;

public class StackOverFlowURLParser extends URLParser {

    public StackOverFlowURLParser(URLParser nextParser) {
        super(nextParser);
    }

    @Override
    protected String getVerifiedHostUrl() {
        return "stackoverflow.com";
    }

}
