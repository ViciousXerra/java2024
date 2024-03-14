package edu.java.scrapper.dao.repository.interfaces;

import java.util.List;

public interface ChatRepository {

    void add(long chatId);

    void remove(long chatId);

    List<Long> findAll();

    boolean isPresent(long chatId);

}
