package edu.java.scrapper.schedulers.linkresourceupdaters;

import edu.java.scrapper.dao.dto.Link;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import static edu.java.scrapper.schedulers.linkresourceupdaters.LinkUpdaterUtils.DOMAIN_NAME_GROUP;
import static edu.java.scrapper.schedulers.linkresourceupdaters.LinkUpdaterUtils.RESOURCE_PATTERN;

public abstract class AbstractLinkResourceUpdater {

    private final AbstractLinkResourceUpdater nextUpdater;
    protected Matcher linkMatcher;

    public AbstractLinkResourceUpdater(AbstractLinkResourceUpdater nextUpdater) {
        this.nextUpdater = nextUpdater;
    }

    public final Map.Entry<LinkUpdaterUtils.Activity, String> process(
        Link link,
        Map<Link, ZonedDateTime> linkZonedDateTimeMap
    ) {
        linkMatcher = RESOURCE_PATTERN.matcher(link.url());
        if (linkMatcher.find()) {
            return processWithMatcher(link, linkZonedDateTimeMap);
        } else {
            throw new IllegalArgumentException("Unable to recognize URL pattern: %s".formatted(link.toString()));
        }
    }

    protected abstract Map.Entry<LinkUpdaterUtils.Activity, String> processPossibleLinkResourceUpdate(
        Link link,
        Map<Link, ZonedDateTime> linkZonedDateTimeMap
    );

    protected abstract boolean isSupported();

    private Map.Entry<LinkUpdaterUtils.Activity, String> processWithMatcher(
        Link link,
        Map<Link, ZonedDateTime> linkZonedDateTimeMap
    ) {
        if (isSupported()) {
            return processPossibleLinkResourceUpdate(link, linkZonedDateTimeMap);
        } else if (nextUpdater != null) {
            return nextUpdater.processWithMatcher(link, linkZonedDateTimeMap);
        } else {
            throw new IllegalArgumentException("Unsupported domain: %s".formatted(linkMatcher.group(
                DOMAIN_NAME_GROUP)));
        }
    }

}
