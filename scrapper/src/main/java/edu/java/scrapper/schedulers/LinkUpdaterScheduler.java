package edu.java.scrapper.schedulers;

import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import edu.java.scrapper.schedulers.linkresourceupdaters.AbstractLinkResourceUpdater;
import edu.java.scrapper.schedulers.linkresourceupdaters.LinkUpdaterUtils;
import edu.java.scrapper.telegrambotclient.clients.BotUpdateClient;
import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Log4j2
public class LinkUpdaterScheduler {

    private static final String SERVICE_UNAVAILABLE_LOG_TEMPLATE = "Service unavailable: {}";
    private final LinkUpdater linkUpdater;
    private final AbstractLinkResourceUpdater abstractLinkResourceUpdater;
    private final BotUpdateClient botUpdateClient;

    @Autowired
    public LinkUpdaterScheduler(
        LinkUpdater linkUpdater,
        AbstractLinkResourceUpdater abstractLinkResourceUpdater,
        BotUpdateClient botUpdateClient
    ) {
        this.linkUpdater = linkUpdater;
        this.abstractLinkResourceUpdater = abstractLinkResourceUpdater;
        this.botUpdateClient = botUpdateClient;
    }

    @Scheduled(fixedDelayString = "#{scheduler.interval()}")
    public void update() {
        List<Link> linkList = linkUpdater.update();
        Map<Link, ZonedDateTime> linkZonedDateTimeMap = new HashMap<>();
        Map<Link, String> linkActivityMap = new HashMap<>();
        linkList.forEach(link -> {
            try {
                Map.Entry<LinkUpdaterUtils.Activity, String> result =
                    abstractLinkResourceUpdater.process(link, linkZonedDateTimeMap);
                if (!result.getKey().equals(LinkUpdaterUtils.Activity.NO_ACTIVITY)) {
                    linkActivityMap.put(link, result.getValue());
                }
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
            } catch (WebClientResponseException e) {
                log.error(SERVICE_UNAVAILABLE_LOG_TEMPLATE, e.getMessage());
            }
        });
        if (linkZonedDateTimeMap.isEmpty()) {
            return;
        }
        List<ChatIdLinkId> relationsList = linkUpdater.modifyUpdatedAtAndReturnRelations(linkZonedDateTimeMap);
        startMessaging(relationsList, linkActivityMap);
    }

    private void startMessaging(List<ChatIdLinkId> relations, Map<Link, String> linkActivityMap) {
        try {
            linkActivityMap.forEach((link, activityDescription) -> {
                List<Long> chatIds = relations.stream().filter(chatIdLinkId -> chatIdLinkId.linkId() == link.linkId())
                    .map(ChatIdLinkId::chatId).toList();
                botUpdateClient.postLinkUpdate(new LinkUpdate(link.linkId(), link.url(), activityDescription, chatIds));
            });
        } catch (WebClientResponseException e) {
            log.error(SERVICE_UNAVAILABLE_LOG_TEMPLATE, e.getMessage());
        }
    }

}
