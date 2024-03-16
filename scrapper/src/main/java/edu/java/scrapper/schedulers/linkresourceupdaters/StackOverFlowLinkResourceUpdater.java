package edu.java.scrapper.schedulers.linkresourceupdaters;

import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.webclients.clients.StackOverFlowClient;
import edu.java.scrapper.webclients.dto.stackoverflow.QuestionInfo;
import edu.java.scrapper.webclients.dto.stackoverflow.StackOverFlowQuestionResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import static edu.java.scrapper.schedulers.linkresourceupdaters.LinkUpdaterUtils.DOMAIN_NAME_GROUP;

public final class StackOverFlowLinkResourceUpdater extends AbstractLinkResourceUpdater {

    private final StackOverFlowClient stackOverFlowClient;

    public StackOverFlowLinkResourceUpdater(
        AbstractLinkResourceUpdater nextUpdater,
        StackOverFlowClient stackOverFlowClient
    ) {
        super(nextUpdater);
        this.stackOverFlowClient = stackOverFlowClient;
    }

    @Override
    protected LinkUpdaterUtils.Activity processPossibleLinkResourceUpdate(
        Link link,
        Map<Link, ZonedDateTime> linkZonedDateTimeMap
    ) {
        StackOverFlowQuestionResponse<QuestionInfo> questionInfo =
            stackOverFlowClient.getQuestionInfoResponse(
                Long.parseLong(linkMatcher.group(LinkUpdaterUtils.STACKOVERFLOW_QUESTION_ID_GROUP))
            );
        ZonedDateTime lastActivityDateTime =
            questionInfo.items().getFirst().lastActivityTime().atZoneSameInstant(ZoneOffset.UTC);
        if (lastActivityDateTime.isAfter(link.updatedAt())) {
            linkZonedDateTimeMap.put(link, lastActivityDateTime);
            return LinkUpdaterUtils.Activity.NEW_UPDATE;
        } else {
            return LinkUpdaterUtils.Activity.NO_ACTIVITY;
        }
    }

    @Override
    protected boolean isSupported() {
        String domainName = linkMatcher.group(DOMAIN_NAME_GROUP);
        return "stackoverflow.com".equals(domainName);
    }
}
