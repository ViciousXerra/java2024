package edu.java.scrapper.schedulers.linkresourceupdaters;

import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.webclients.clients.GitHubClient;
import edu.java.scrapper.webclients.dto.github.RepositoryActivityResponse;
import edu.java.scrapper.webclients.dto.github.RepositoryActivityType;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import static edu.java.scrapper.schedulers.linkresourceupdaters.LinkUpdaterUtils.DOMAIN_NAME_GROUP;

public final class GitHubLinkResourceUpdater extends AbstractLinkResourceUpdater {

    private final GitHubClient gitHubClient;

    public GitHubLinkResourceUpdater(AbstractLinkResourceUpdater nextUpdater, GitHubClient gitHubClient) {
        super(nextUpdater);
        this.gitHubClient = gitHubClient;
    }

    @Override
    protected LinkUpdaterUtils.Activity processPossibleLinkResourceUpdate(
        Link link,
        Map<Link, ZonedDateTime> linkZonedDateTimeMap
    ) {
        List<RepositoryActivityResponse> activityList = gitHubClient.getActivityListResponse(
            linkMatcher.group(LinkUpdaterUtils.GITHUB_REPO_OWNER_GROUP),
            linkMatcher.group(LinkUpdaterUtils.GITHUB_REPO_NAME_GROUP)
        );
        RepositoryActivityResponse lastActivityResponse = activityList.getFirst();
        ZonedDateTime lastActivityDateTime = lastActivityResponse.timestamp().atZoneSameInstant(ZoneOffset.UTC);
        if (lastActivityDateTime.isAfter(link.updatedAt())) {
            linkZonedDateTimeMap.put(link, lastActivityDateTime);
            RepositoryActivityType lastActivityType = lastActivityResponse.activityType();
            return switch (lastActivityType) {
                case PUSH -> LinkUpdaterUtils.Activity.GITHUB_PUSH;
                case BRANCH_CREATION -> LinkUpdaterUtils.Activity.GITHUB_BRANCH_CREATION;
                default -> LinkUpdaterUtils.Activity.NEW_UPDATE;
            };
        } else {
            return LinkUpdaterUtils.Activity.NO_ACTIVITY;
        }
    }

    @Override
    protected boolean isSupported() {
        String domainName = linkMatcher.group(DOMAIN_NAME_GROUP);
        return "github.com".equals(domainName);
    }

}
