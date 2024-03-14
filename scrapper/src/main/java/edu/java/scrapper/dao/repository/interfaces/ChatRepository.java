package edu.java.scrapper.dao.repository.interfaces;

import java.util.List;
import java.util.Optional;

public interface ChatRepository {

    void add(long chatId);

    void remove(long chatId);

    List<Long> findAll();

    Optional<Long> findById(long chatId);

}
