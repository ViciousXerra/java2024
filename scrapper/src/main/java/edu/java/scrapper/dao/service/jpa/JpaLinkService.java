package edu.java.scrapper.dao.service.jpa;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.jpa.entities.ChatEntity;
import edu.java.scrapper.dao.repository.jpa.entities.LinkEntity;
import edu.java.scrapper.dao.repository.jpa.repositories.JpaChatRepository;
import edu.java.scrapper.dao.repository.jpa.repositories.JpaLinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.function.Function;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public class JpaLinkService implements LinkService {

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
    private static final String DELETE_EXCEPTION_MESSAGE = "Unable to delete url data.";
    private final JpaChatRepository chatRepository;
    private final JpaLinkRepository linkRepository;

    public JpaLinkService(JpaChatRepository chatRepository, JpaLinkRepository linkRepository) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public Link add(long tgChatId, String url) {
        isIdVerified(tgChatId);
        ChatEntity chat = chatRepository.getReferenceById(tgChatId);
        LinkEntity link = linkRepository.findByUrl(url).orElseGet(() -> {
            LinkEntity linkEntity = new LinkEntity();
            ZonedDateTime currentDateTime = ZonedDateTime.now();
            linkEntity.setUrl(url);
            linkEntity.setCheckedAt(ZONED_DATE_TIME_CONVERTER.apply(currentDateTime));
            linkEntity.setUpdatedAt(ZONED_DATE_TIME_CONVERTER.apply(currentDateTime));
            linkRepository.save(linkEntity);
            return linkEntity;
        });
        if (chat.isAlreadyTracking(link)) {
            throw new ConflictException("Unable to insert url data.", "URL must be unique.");
        }
        chat.addLink(link);
        chatRepository.saveAndFlush(chat);
        linkRepository.saveAndFlush(link);
        return LINK_CONVERTER.apply(link);
    }

    @Override
    public Link remove(long tgChatId, String url) {
        isIdVerified(tgChatId);
        ChatEntity chat = chatRepository.getReferenceById(tgChatId);
        LinkEntity link = linkRepository.findByUrl(url)
            .orElseThrow(() -> new NotFoundException(DELETE_EXCEPTION_MESSAGE, "URL hasn't been registered."));
        if (chat.isAlreadyTracking(link)) {
            chat.removeLink(link);
            chatRepository.saveAndFlush(chat);
            if (link.getRelatedChats().isEmpty()) {
                linkRepository.delete(link);
            } else {
                linkRepository.save(link);
            }
            linkRepository.flush();
            return LINK_CONVERTER.apply(link);
        } else {
            throw new NotFoundException(DELETE_EXCEPTION_MESSAGE, "URL hasn't been founded.");
        }
    }

    @Override
    public Collection<Link> listAll(long tgChatId) {
        isIdVerified(tgChatId);
        ChatEntity chat = chatRepository.getReferenceById(tgChatId);
        return chat.getRegisteredLinks().stream()
            .map(LINK_CONVERTER)
            .toList();
    }

    private void isIdVerified(long tgChatId) {
        if (!chatRepository.existsById(tgChatId)) {
            throw new NotFoundException(
                "Links not found.",
                "Registration required for managing links for tracking."
            );
        }
    }

}
