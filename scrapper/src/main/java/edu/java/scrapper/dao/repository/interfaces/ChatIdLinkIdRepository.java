package edu.java.scrapper.dao.repository.interfaces;

import edu.java.scrapper.dao.dto.ChatIdLinkId;
import java.util.List;

public interface ChatIdLinkIdRepository {

    ChatIdLinkId add(long chatId, long linkId);

    ChatIdLinkId remove(long chatId, long linkId);

    List<ChatIdLinkId> findAll();

}
