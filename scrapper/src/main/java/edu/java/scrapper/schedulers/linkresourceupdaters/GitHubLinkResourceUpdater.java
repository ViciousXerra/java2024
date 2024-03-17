package edu.java.scrapper.schedulers.linkresourceupdaters;

import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.webclients.clients.GitHubClient;
import edu.java.scrapper.webclients.dto.github.RepositoryActivityResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static edu.java.scrapper.schedulers.linkresourceupdaters.LinkUpdaterUtils.DOMAIN_NAME_GROUP;

public final class GitHubLinkResourceUpdater extends AbstractLinkResourceUpdater {

    private final static String USER_TEMPLATE = "User: %s";
    private final static String TIMESTAMP_TEMPLATE = "Date/Time: %s";
    private final static String ACTIVITY_TEMPLATE = "Activity: %s";
    private final static String AVAILABLE_ON_TEMPLATE = "Available on: %s";
    private final static String LINE_SEPARATOR = System.lineSeparator();
    private final static Function<OffsetDateTime, ZonedDateTime> ZONED_DATE_TIME_CONVERTER_LAMBDA =
        offsetDateTime -> offsetDateTime.atZoneSameInstant(ZoneOffset.UTC);
    private final GitHubClient gitHubClient;
    private final StringBuilder stringBuilder;

    public GitHubLinkResourceUpdater(AbstractLinkResourceUpdater nextUpdater, GitHubClient gitHubClient) {
        super(nextUpdater);
        this.gitHubClient = gitHubClient;
        stringBuilder = new StringBuilder();
    }

    @Override
    protected Map.Entry<LinkUpdaterUtils.Activity, String> processPossibleLinkResourceUpdate(
        Link link,
        Map<Link, ZonedDateTime> linkZonedDateTimeMap
    ) {
        List<RepositoryActivityResponse> activityList = gitHubClient.getActivityListResponse(
            linkMatcher.group(LinkUpdaterUtils.GITHUB_REPO_OWNER_GROUP),
            linkMatcher.group(LinkUpdaterUtils.GITHUB_REPO_NAME_GROUP)
        );
        RepositoryActivityResponse lastActivityResponse = activityList.getFirst();
        ZonedDateTime lastActivityDateTime = ZONED_DATE_TIME_CONVERTER_LAMBDA.apply(lastActivityResponse.timestamp());
        if (lastActivityDateTime.isAfter(link.updatedAt())) {
            linkZonedDateTimeMap.put(link, lastActivityDateTime);
            activityList
                .stream()
                .filter(activity -> ZONED_DATE_TIME_CONVERTER_LAMBDA.apply(activity.timestamp())
                    .isAfter(link.updatedAt()))
                .sorted(RepositoryActivityResponse::compareTo)
                .forEach(this::buildActivityDescription);
            Map.Entry<LinkUpdaterUtils.Activity, String> result =
                new AbstractMap.SimpleEntry<>(LinkUpdaterUtils.Activity.NEW_UPDATE, stringBuilder.toString());
            stringBuilder.setLength(0);
            return result;
        } else {
            return new AbstractMap.SimpleEntry<>(LinkUpdaterUtils.Activity.NO_ACTIVITY, null);
        }
    }

    @Override
    protected boolean isSupported() {
        String domainName = linkMatcher.group(DOMAIN_NAME_GROUP);
        return "github.com".equals(domainName);
    }

    private void buildActivityDescription(RepositoryActivityResponse response) {
        stringBuilder.append(USER_TEMPLATE.formatted(response.actor().login())).append(LINE_SEPARATOR);
        stringBuilder.append(String.format(
            TIMESTAMP_TEMPLATE,
            ZONED_DATE_TIME_CONVERTER_LAMBDA.apply(response.timestamp()).toString()
        )).append(LINE_SEPARATOR);
        switch (response.activityType()) {
            case PUSH -> stringBuilder.append(ACTIVITY_TEMPLATE.formatted("pushed new commits"));
            case BRANCH_CREATION -> stringBuilder.append(ACTIVITY_TEMPLATE.formatted("created new branch"));
            default -> stringBuilder.append(ACTIVITY_TEMPLATE.formatted("new update"));
        }
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append(AVAILABLE_ON_TEMPLATE.formatted(response.ref())).append(LINE_SEPARATOR);
    }

}
