package edu.java.scrapper.dao.repository.interfaces;

import edu.java.scrapper.dao.dto.Chat;

import java.util.List;

public interface ChatRepository {

    void add(long chatId);

    void remove(long chatId);

    List<Chat> findAll();

}
