package edu.java.scrapper.schedulers;

import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.interfaces.ChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.interfaces.LinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import edu.java.scrapper.schedulers.linkresourceupdaters.AbstractLinkResourceUpdater;
import edu.java.scrapper.schedulers.linkresourceupdaters.LinkUpdaterUtils;
import edu.java.scrapper.telegrambotclient.clients.BotUpdateClient;
import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LinkUpdaterScheduler {

    private final static Logger LOGGER = LogManager.getLogger(LinkUpdaterScheduler.class);
    private final LinkUpdater linkUpdater;
    private final LinkRepository linkRepository;
    private final ChatIdLinkIdRepository chatIdLinkIdRepository;
    private final AbstractLinkResourceUpdater abstractLinkResourceUpdater;
    private final BotUpdateClient botUpdateClient;

    @Autowired
    public LinkUpdaterScheduler(
        LinkUpdater linkUpdater,
        @Qualifier("jdbc-link-repo")
        LinkRepository linkRepository,
        @Qualifier("jdbc-chatidlinkid-repo")
        ChatIdLinkIdRepository chatIdLinkIdRepository,
        AbstractLinkResourceUpdater abstractLinkResourceUpdater,
        BotUpdateClient botUpdateClient
    ) {
        this.linkUpdater = linkUpdater;
        this.linkRepository = linkRepository;
        this.chatIdLinkIdRepository = chatIdLinkIdRepository;
        this.abstractLinkResourceUpdater = abstractLinkResourceUpdater;
        this.botUpdateClient = botUpdateClient;
    }

    @Scheduled(fixedDelayString = "#{scheduler.interval()}")
    public void update() {
        List<Link> linkList = linkUpdater.update();
        Map<Link, ZonedDateTime> linkZonedDateTimeMap = new HashMap<>();
        Map<Link, LinkUpdaterUtils.Activity> linkActivityMap = new HashMap<>();
        linkList.forEach(link -> {
            try {
                LinkUpdaterUtils.Activity activity = abstractLinkResourceUpdater.process(link, linkZonedDateTimeMap);
                if (!activity.equals(LinkUpdaterUtils.Activity.NO_ACTIVITY)) {
                    linkActivityMap.put(link, activity);
                }
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage());
            }
        });
        if (linkZonedDateTimeMap.isEmpty()) {
            return;
        }
        List<ChatIdLinkId> relationsList = modifyUpdatedAtAndReturnRelations(linkZonedDateTimeMap);
        startMessaging(relationsList, linkActivityMap);
    }

    @Transactional
    public List<ChatIdLinkId> modifyUpdatedAtAndReturnRelations(Map<Link, ZonedDateTime> linkZonedDateTimeMap) {
        linkZonedDateTimeMap.forEach((key, value) -> linkRepository.modifyUpdatedAtTimestamp(
            key.url(),
            value
        ));
        return chatIdLinkIdRepository.findAll();
    }

    private void startMessaging(List<ChatIdLinkId> relations, Map<Link, LinkUpdaterUtils.Activity> linkActivityMap) {
        linkActivityMap.forEach((key, value) -> {
            List<Long> chatIds = relations.stream().filter(chatIdLinkId -> chatIdLinkId.linkId() == key.linkId())
                .map(ChatIdLinkId::chatId).toList();
            String desc = buildDescription(value);
            botUpdateClient.postLinkUpdate(new LinkUpdate(key.linkId(), key.url(), desc, chatIds));
        });
    }

    private static String buildDescription(LinkUpdaterUtils.Activity activity) {
        return switch (activity) {
            case GITHUB_PUSH -> "The repository branch was updated by push";
            case GITHUB_BRANCH_CREATION -> "A new branch has been created in the repository";
            default -> "There is a new update";
        };
    }

}
