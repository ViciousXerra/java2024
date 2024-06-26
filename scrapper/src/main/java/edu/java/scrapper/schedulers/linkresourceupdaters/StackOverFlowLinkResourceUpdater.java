package edu.java.scrapper.schedulers.linkresourceupdaters;

import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.webclients.clients.StackOverFlowClient;
import edu.java.scrapper.webclients.dto.stackoverflow.QuestionInfo;
import edu.java.scrapper.webclients.dto.stackoverflow.StackOverFlowQuestionResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.AbstractMap;
import java.util.Map;
import static edu.java.scrapper.schedulers.linkresourceupdaters.LinkUpdaterUtils.DOMAIN_NAME_GROUP;

public final class StackOverFlowLinkResourceUpdater extends AbstractLinkResourceUpdater {

    private final StackOverFlowClient stackOverFlowClient;

    public StackOverFlowLinkResourceUpdater(
        StackOverFlowClient stackOverFlowClient,
        AbstractLinkResourceUpdater nextUpdater
    ) {
        super(nextUpdater);
        this.stackOverFlowClient = stackOverFlowClient;
    }

    @Override
    protected Map.Entry<LinkUpdaterUtils.Activity, String> processPossibleLinkResourceUpdate(
        Link link,
        Map<Link, ZonedDateTime> linkZonedDateTimeMap
    ) {
        StackOverFlowQuestionResponse<QuestionInfo> questionInfo =
            stackOverFlowClient.getQuestionInfoResponse(
                Long.parseLong(linkMatcher.group(LinkUpdaterUtils.STACKOVERFLOW_QUESTION_ID_GROUP))
            );
        QuestionInfo info = questionInfo.items().getFirst();
        ZonedDateTime lastActivityDateTime = info.lastActivityTime().atZoneSameInstant(ZoneOffset.UTC);
        if (lastActivityDateTime.isAfter(link.updatedAt())) {
            linkZonedDateTimeMap.put(link, lastActivityDateTime);
            return new AbstractMap.SimpleEntry<>(
                LinkUpdaterUtils.Activity.NEW_UPDATE,
                "There is new activity in \"%s\" question thread".formatted(info.title())
            );
        } else {
            return new AbstractMap.SimpleEntry<>(LinkUpdaterUtils.Activity.NO_ACTIVITY, null);
        }
    }

    @Override
    protected boolean isSupported() {
        String domainName = linkMatcher.group(DOMAIN_NAME_GROUP);
        return "stackoverflow.com".equals(domainName);
    }
}
