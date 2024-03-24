package edu.java.scrapper.dao.service.jdbc;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public class JdbcLinkUpdater implements LinkUpdater {

    private final JdbcLinkRepository linkRepository;
    private final JdbcChatIdLinkIdRepository chatIdLinkIdRepository;
    private final int fetchLimit;

    public JdbcLinkUpdater(
        JdbcLinkRepository linkRepository,
        JdbcChatIdLinkIdRepository chatIdLinkIdRepository,
        ApplicationConfig applicationConfig
    ) {
        this.linkRepository = linkRepository;
        this.chatIdLinkIdRepository = chatIdLinkIdRepository;
        this.fetchLimit = applicationConfig.scheduler().fetchLimit();
    }

    @Override
    public List<Link> update() {
        return linkRepository.findUpToCheck(fetchLimit);
    }

    @Override
    public List<ChatIdLinkId> modifyUpdatedAtAndReturnRelations(Map<Link, ZonedDateTime> linkZonedDateTimeMap) {
        linkZonedDateTimeMap.forEach((key, value) -> linkRepository.modifyUpdatedAtTimestamp(
            key.url(),
            value
        ));
        return chatIdLinkIdRepository.findAll();
    }

}
