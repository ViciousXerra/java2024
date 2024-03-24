package edu.java.scrapper.dao.service.jpa;

import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.jpa.entities.LinkEntity;
import edu.java.scrapper.dao.repository.jpa.repositories.JpaLinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.data.domain.Limit;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public class JpaLinkUpdater implements LinkUpdater {

    private static final Function<ZonedDateTime, Timestamp> ZONED_DATE_TIME_CONVERTER = zonedDateTime -> Timestamp.from(
        zonedDateTime.toInstant());
    private static final Function<Timestamp, ZonedDateTime> TIMESTAMP_CONVERTER =
        timestamp -> timestamp.toInstant().atZone(ZoneOffset.UTC);
    private static final Function<LinkEntity, Link> LINK_CONVERTER =
        linkEntity -> new Link(
            linkEntity.getId(),
            linkEntity.getUrl(),
            TIMESTAMP_CONVERTER.apply(linkEntity.getUpdatedAt()),
            TIMESTAMP_CONVERTER.apply(linkEntity.getCheckedAt())
        );
    private final JpaLinkRepository linkRepository;
    private final int fetchLimit;

    public JpaLinkUpdater(JpaLinkRepository linkRepository, ApplicationConfig applicationConfig) {
        this.linkRepository = linkRepository;
        this.fetchLimit = applicationConfig.scheduler().fetchLimit();
    }

    @Override
    public List<Link> update() {
        List<LinkEntity> linkEntities = linkRepository.findByOrderByCheckedAtAsc(Limit.of(fetchLimit));
        ZonedDateTime currentCheckDateTime = ZonedDateTime.now();
        for (LinkEntity linkEntity : linkEntities) {
            linkEntity.setCheckedAt(ZONED_DATE_TIME_CONVERTER.apply(currentCheckDateTime));
        }
        linkRepository.saveAllAndFlush(linkEntities);
        return linkEntities.stream().map(LINK_CONVERTER).toList();
    }

    @Override
    public List<ChatIdLinkId> modifyUpdatedAtAndReturnRelations(Map<Link, ZonedDateTime> linkZonedDateTimeMap) {
        List<LinkEntity> changedLinkEntities = new ArrayList<>();
        for (Map.Entry<Link, ZonedDateTime> entry : linkZonedDateTimeMap.entrySet()) {
            LinkEntity entity = linkRepository.findByUrl(entry.getKey().url())
                .orElseThrow(() -> new NotFoundException("Unable to find URL.", "URL isn't presented."));
            entity.setUpdatedAt(ZONED_DATE_TIME_CONVERTER.apply(entry.getValue()));
            changedLinkEntities.add(entity);
        }
        linkRepository.saveAllAndFlush(changedLinkEntities);
        return changedLinkEntities
            .stream()
            .flatMap(
                changedLinkEntity -> changedLinkEntity.getRelatedChats()
                    .stream()
                    .map(chatEntity -> new ChatIdLinkId(chatEntity.getId(), changedLinkEntity.getId()))
            ).toList();
    }

}
