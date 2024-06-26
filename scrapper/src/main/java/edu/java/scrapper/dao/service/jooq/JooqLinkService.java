package edu.java.scrapper.dao.service.jooq;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.jooq.JooqChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.jooq.JooqChatRepository;
import edu.java.scrapper.dao.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import java.util.Collection;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public class JooqLinkService implements LinkService {

    private static final String DELETION_EXCEPTION_MESSAGE = "Unable to delete url data.";
    private final JooqChatRepository chatRepository;
    private final JooqChatIdLinkIdRepository chatIdLinkIdRepository;
    private final JooqLinkRepository linkRepository;

    public JooqLinkService(
        JooqChatRepository chatRepository,
        JooqChatIdLinkIdRepository chatIdLinkIdRepository,
        JooqLinkRepository linkRepository
    ) {
        this.chatRepository = chatRepository;
        this.linkRepository = linkRepository;
        this.chatIdLinkIdRepository = chatIdLinkIdRepository;
    }

    @Override
    public Link add(long tgChatId, String url) {
        isIdVerified(tgChatId);
        Link link = linkRepository.findByUrl(url).orElseGet(() -> linkRepository.add(url));
        boolean isAlreadyTracking = chatIdLinkIdRepository.findAllByChatId(tgChatId).stream()
            .anyMatch(chatIdLinkId -> chatIdLinkId.linkId() == link.linkId());
        if (isAlreadyTracking) {
            throw new ConflictException("Unable to insert url data.", "URL must be unique.");
        }
        chatIdLinkIdRepository.add(tgChatId, link.linkId());
        return link;
    }

    @Override
    public Link remove(long tgChatId, String url) {
        isIdVerified(tgChatId);
        Link link = linkRepository.findByUrl(url)
            .orElseThrow(() -> new NotFoundException(DELETION_EXCEPTION_MESSAGE, "URL hasn't been registered."));
        List<ChatIdLinkId> chatIdLinkIdList = chatIdLinkIdRepository.findAllByLinkId(link.linkId());
        boolean isTracking = chatIdLinkIdList.stream().anyMatch(chatIdLinkId -> tgChatId == chatIdLinkId.chatId());
        if (isTracking && chatIdLinkIdList.size() == 1) {
            linkRepository.remove(link.url());
        } else if (isTracking) {
            chatIdLinkIdRepository.remove(tgChatId, link.linkId());
        } else {
            throw new NotFoundException(DELETION_EXCEPTION_MESSAGE, "URL hasn't been founded.");
        }
        return link;
    }

    @Override
    public Collection<Link> listAll(long tgChatId) {
        isIdVerified(tgChatId);
        List<Link> linkList = linkRepository.findAll();
        List<ChatIdLinkId> chatIdLinkIdList = chatIdLinkIdRepository.findAllByChatId(tgChatId);
        return linkList
            .stream()
            .filter(link -> chatIdLinkIdList.stream().anyMatch(chatIdLinkId -> link.linkId() == chatIdLinkId.linkId()))
            .toList();
    }

    private void isIdVerified(long tgChatId) {
        if (!chatRepository.isPresent(tgChatId)) {
            throw new NotFoundException(
                "Links not found.",
                "Registration required for managing links for tracking."
            );
        }
    }

}
