package edu.java.scrapper.dao.service.jdbc;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class JdbcChatService implements ChatService {

    private final JdbcChatRepository chatRepository;

    @Autowired
    public JdbcChatService(JdbcChatRepository chatRepository) {
        this.chatRepository = chatRepository;
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
        chatRepository.remove(chatId);
    }

}
