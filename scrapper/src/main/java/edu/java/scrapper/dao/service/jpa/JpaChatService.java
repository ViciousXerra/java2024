package edu.java.scrapper.dao.service.jpa;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.dao.repository.jpa.entities.ChatEntity;
import edu.java.scrapper.dao.repository.jpa.repositories.JpaChatRepository;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
public class JpaChatService implements ChatService {

    private final JpaChatRepository chatRepository;

    public JpaChatService(JpaChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public void register(long chatId) {
        if (chatRepository.existsById(chatId)) {
            throw new ConflictException("Chat already signed up.", "Chat associated with this id already signed up.");
        }
        ChatEntity chat = new ChatEntity();
        chat.setId(chatId);
        chatRepository.saveAndFlush(chat);
    }

    @Override
    public void unregister(long chatId) {
        if (!chatRepository.existsById(chatId)) {
            throw new NotFoundException("Chat not found.", "Chat associated with this id can't be founded.");
        }
        chatRepository.deleteById(chatId);
        chatRepository.flush();
    }

}
