package edu.java.bot.parsers;

public class GitHubURLParser extends URLParser {

    public GitHubURLParser(URLParser nextParser) {
        super(nextParser);
    }

    @Override
    protected String getVerifiedHostUrl() {
        return "github.com";
    }

}
