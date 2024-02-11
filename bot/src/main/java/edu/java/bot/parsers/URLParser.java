package edu.java.bot.parsers;

import java.net.URI;

public abstract class URLParser {

    protected URLParser nextParser;

    public URLParser(URLParser nextParser) {
        this.nextParser = nextParser;
    }

    protected abstract String getVerifiedHostUrl();

    public boolean isVerifiedHostUrl(URI url) {
        if (url == null) {
            throw new IllegalArgumentException("URL can't be null.", new NullPointerException());
        }
        if (url.getHost().equals(getVerifiedHostUrl())) {
            return true;
        }
        if (nextParser != null) {
            return nextParser.isVerifiedHostUrl(url);
        }
        return false;
    }

}
