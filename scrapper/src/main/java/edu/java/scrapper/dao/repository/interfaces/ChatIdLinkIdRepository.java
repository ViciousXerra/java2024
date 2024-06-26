package edu.java.scrapper.dao.repository.interfaces;

import edu.java.scrapper.dao.dto.ChatIdLinkId;
import java.util.List;

public interface ChatIdLinkIdRepository {

    void add(long chatId, long linkId);

    void remove(long chatId, long linkId);

    List<ChatIdLinkId> findAll();

    List<ChatIdLinkId> findAllByChatId(long chatId);

    List<ChatIdLinkId> findAllByLinkId(long linkId);

}
