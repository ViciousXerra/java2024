package edu.java.scrapper.schedulers;

import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.interfaces.ChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.interfaces.LinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import edu.java.scrapper.telegrambotclient.clients.BotUpdateClient;
import edu.java.scrapper.webclients.clients.GitHubClient;
import edu.java.scrapper.webclients.clients.StackOverFlowClient;
import edu.java.scrapper.webclients.dto.github.RepositoryActivityResponse;
import edu.java.scrapper.webclients.dto.stackoverflow.QuestionInfo;
import edu.java.scrapper.webclients.dto.stackoverflow.StackOverFlowQuestionResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.schedulers.LinkUpdaterUtils.RESOURCE_PATTERN;

@Component
public class LinkUpdaterScheduler {

    private final static Logger LOGGER = LogManager.getLogger(LinkUpdaterScheduler.class);
    private final LinkUpdater linkUpdater;
    private final LinkRepository linkRepository;
    private final ChatIdLinkIdRepository chatIdLinkIdRepository;
    private final GitHubClient gitHubClient;
    private final StackOverFlowClient stackOverFlowClient;
    private final BotUpdateClient botUpdateClient;

    @Autowired
    public LinkUpdaterScheduler(
        LinkUpdater linkUpdater,
        LinkRepository linkRepository,
        ChatIdLinkIdRepository chatIdLinkIdRepository,
        GitHubClient gitHubClient,
        StackOverFlowClient stackOverFlowClient,
        BotUpdateClient botUpdateClient
    ) {
        this.linkUpdater = linkUpdater;
        this.linkRepository = linkRepository;
        this.chatIdLinkIdRepository = chatIdLinkIdRepository;
        this.gitHubClient = gitHubClient;
        this.stackOverFlowClient = stackOverFlowClient;
        this.botUpdateClient = botUpdateClient;
    }

    @Scheduled(fixedDelayString = "#{scheduler.interval()}")
    public void update() {
        List<Link> linkList = linkUpdater.update();
        Map<Link, ZonedDateTime> updatedLinksMap = new HashMap<>();
        linkList.forEach(link -> {
            try {
                LinkUpdaterUtils.Domain domain = LinkUpdaterUtils.resolveDomain(link.url());
                switch (domain) {
                    case GITHUB -> processGitHubUrl(link, updatedLinksMap);
                    case STACKOVERFLOW -> processStackOverFlowUrl(link, updatedLinksMap);
                }
            } catch (IllegalArgumentException e) {
                LOGGER.error(e.getMessage());
            }
        });
        modifyUpdatedAtAndReturnChatMap(updatedLinksMap);
        startMessaging(updatedLinksMap);
    }

    private void processGitHubUrl(Link link, Map<Link, ZonedDateTime> map) {
        Matcher m = RESOURCE_PATTERN.matcher(link.url());
        if (m.find()) {
            List<RepositoryActivityResponse> activityList = gitHubClient.getActivityListResponse(
                m.group(LinkUpdaterUtils.GITHUB_REPO_OWNER_GROUP),
                m.group(LinkUpdaterUtils.GITHUB_REPO_NAME_GROUP)
            );
            ZonedDateTime lastActivityDateTime = activityList.getFirst().timestamp().atZoneSameInstant(ZoneOffset.UTC);
            if (lastActivityDateTime.isAfter(link.updatedAt())) {
                map.put(link, lastActivityDateTime);
            }
        } else {
            throw new IllegalArgumentException("Unable to parse GitHub URL");
        }
    }

    private void processStackOverFlowUrl(Link link, Map<Link, ZonedDateTime> map) {
        Matcher m = RESOURCE_PATTERN.matcher(link.url());
        if (m.find()) {
            StackOverFlowQuestionResponse<QuestionInfo> questionInfo =
                stackOverFlowClient.getQuestionInfoResponse(
                    Long.parseLong(m.group(LinkUpdaterUtils.STACKOVERFLOW_QUESTION_ID_GROUP))
                );
            ZonedDateTime lastActivityDateTime =
                questionInfo.items().getFirst().lastActivityTime().atZoneSameInstant(ZoneOffset.UTC);
            if (lastActivityDateTime.isAfter(link.updatedAt())) {
                map.put(link, lastActivityDateTime);
            }
        } else {
            throw new IllegalArgumentException("Unable to parse StackOverFlow URL");
        }
    }

    @Transactional
    public void modifyUpdatedAtAndReturnChatMap(Map<Link, ZonedDateTime> map) {
        map.forEach((key, value) -> linkRepository.modifyUpdatedAtTimestamp(
            key.url(),
            value
        ));
    }

    private void startMessaging(Map<Link, ZonedDateTime> map) {

    }

}
