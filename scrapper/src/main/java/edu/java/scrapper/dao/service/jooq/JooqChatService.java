package edu.java.scrapper.dao.service.jooq;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.repository.jooq.JooqChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.jooq.JooqChatRepository;
import edu.java.scrapper.dao.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public class JooqChatService implements ChatService {

    private final JooqChatRepository chatRepository;
    private final JooqChatIdLinkIdRepository chatIdLinkIdRepository;
    private final JooqLinkRepository linkRepository;

    public JooqChatService(
        JooqChatRepository chatRepository,
        JooqChatIdLinkIdRepository chatIdLinkIdRepository,
        JooqLinkRepository linkRepository
    ) {
        this.chatRepository = chatRepository;
        this.chatIdLinkIdRepository = chatIdLinkIdRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public void register(long chatId) {
        List<Long> allRegisteredIds = chatRepository.findAll();
        if (allRegisteredIds.contains(chatId)) {
            throw new ConflictException("Chat already signed up.", "Chat associated with this id already signed up.");
        }
        chatRepository.add(chatId);
    }

    @Override
    public void unregister(long chatId) {
        List<Long> allRegisteredIds = chatRepository.findAll();
        if (!allRegisteredIds.contains(chatId)) {
            throw new NotFoundException("Chat not found.", "Chat associated with this id can't be founded.");
        }
        List<ChatIdLinkId> relationsList = chatIdLinkIdRepository.findAll();
        List<Long> chatRelativeLinkIds =
            relationsList
                .stream()
                .filter(chatIdLinkId -> chatId == chatIdLinkId.chatId())
                .map(ChatIdLinkId::linkId)
                .toList();
        long[] linksToRemoveIds =
            chatRelativeLinkIds
                .stream()
                .filter(linkId -> relationsList.stream()
                    .noneMatch(chatIdLinkId -> chatIdLinkId.chatId() != chatId && chatIdLinkId.linkId() == linkId)
                )
                .mapToLong(l -> l).toArray();
        linkRepository.removeByIds(linksToRemoveIds);
        chatRepository.remove(chatId);
    }

}
