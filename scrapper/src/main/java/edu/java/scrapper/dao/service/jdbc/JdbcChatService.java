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
public class JdbcChatService implements ChatService {

    private final JdbcChatRepository chatRepository;

    @Autowired
    public JdbcChatService(JdbcChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    @Transactional
    public void register(long chatId) {
        List<Long> allRegisteredIds = chatRepository.findAll();
        if (allRegisteredIds.contains(chatId)) {
            throw new ConflictException("Chat already signed up", "Chat with this id already registered");
        }
        chatRepository.add(chatId);
    }

    @Override
    @Transactional
    public void unregister(long chatId) {
        List<Long> allRegisteredIds = chatRepository.findAll();
        if (!allRegisteredIds.contains(chatId)) {
            throw new NotFoundException("Can't find a chat", "Chat with this id does not exist");
        }
        chatRepository.remove(chatId);
    }
}
