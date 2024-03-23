package edu.java.scrapper.dao.service.interfaces;

import org.springframework.stereotype.Service;

public interface ChatService {

    void register(long chatId);

    void unregister(long chatId);

}
