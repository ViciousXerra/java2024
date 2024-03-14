package edu.java.scrapper.dao.service.jdbc;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class JdbcLinkService implements LinkService {

    private final JdbcChatRepository chatRepository;
    private final JdbcLinkRepository linkRepository;
    private final JdbcChatIdLinkIdRepository chatIdLinkIdRepository;

    @Autowired
    public JdbcLinkService(
        JdbcChatRepository chatRepository,
        JdbcLinkRepository linkRepository,
        JdbcChatIdLinkIdRepository chatIdLinkIdRepository
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
            throw new ConflictException("URL must be unique", "Unable to insert url data");
        }
        chatIdLinkIdRepository.add(tgChatId, link.linkId());
        return link;
    }

    @Override
    public Link remove(long tgChatId, String url) {
        isIdVerified(tgChatId);
        Link link = linkRepository.findByUrl(url)
            .orElseThrow(() -> new NotFoundException("URL hasn't been registered", "Unable to delete url data"));
        List<ChatIdLinkId> chatIdLinkIdList = chatIdLinkIdRepository.findAllByLinkId(link.linkId());
        boolean isTracking = chatIdLinkIdList.stream().anyMatch(chatIdLinkId -> tgChatId == chatIdLinkId.chatId());
        if (isTracking && chatIdLinkIdList.size() == 1) {
            linkRepository.remove(link.url());
        } else if (isTracking) {
            chatIdLinkIdRepository.remove(tgChatId, link.linkId());
        } else {
            throw new NotFoundException(
                "URL hasn't been founded",
                "Unable to delete url data because this chat didn't track given URL"
            );
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
                "Links not found",
                "Registration required for managing links for tracking"
            );
        }
    }

}
