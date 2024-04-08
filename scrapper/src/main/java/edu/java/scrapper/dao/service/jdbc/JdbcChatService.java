package edu.java.scrapper.dao.service.jdbc;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(JdbcChatService.JDBC_CHAT_SERVICE)
@Transactional(rollbackFor = Exception.class)
public class JdbcChatService implements ChatService {

    public static final String JDBC_CHAT_SERVICE = "jdbc-chat-service";
    private final JdbcChatRepository chatRepository;
    private final JdbcChatIdLinkIdRepository chatIdLinkIdRepository;
    private final JdbcLinkRepository linkRepository;

    @Autowired
    public JdbcChatService(
        JdbcChatRepository chatRepository,
        JdbcChatIdLinkIdRepository chatIdLinkIdRepository,
        JdbcLinkRepository linkRepository
    ) {
        this.chatRepository = chatRepository;
        this.chatIdLinkIdRepository = chatIdLinkIdRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    public void register(long chatId) {
        List<Long> allRegisteredIds = chatRepository.findAll();
        if (allRegisteredIds.contains(chatId)) {
            throw new ConflictException("Chat already signed up", "Chat associated with this id already signed up");
        }
        chatRepository.add(chatId);
    }

    @Override
    public void unregister(long chatId) {
        List<Long> allRegisteredIds = chatRepository.findAll();
        if (!allRegisteredIds.contains(chatId)) {
            throw new NotFoundException("Chat not found", "Chat associated with this id can't be founded");
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
