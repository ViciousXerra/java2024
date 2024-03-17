package edu.java.scrapper.dao.repository.jooq;

import edu.java.scrapper.api.exceptions.UnhandledException;
import edu.java.scrapper.dao.repository.interfaces.ChatRepository;
import java.util.List;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.domain.jooq.Tables.CHAT;

@Repository
public class JooqChatRepository implements ChatRepository {

    private final DefaultDSLContext dslContext;

    @Autowired
    public JooqChatRepository(DefaultDSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public void add(long chatId) {
        try {
            dslContext
                .insertInto(CHAT)
                .set(CHAT.ID, chatId)
                .execute();
        } catch (DataAccessException e) {
            throw new UnhandledException("Chat already signed up", "Chat associated with this id already signed up");
        }
    }

    @Override
    public void remove(long chatId) {
        int updates =
            dslContext
                .delete(CHAT)
                .where(CHAT.ID.eq(chatId))
                .execute();
        if (updates == 0) {
            throw new UnhandledException("Chat not found", "Chat associated with this id can't be founded");
        }
    }

    @Override
    public List<Long> findAll() {
        return dslContext
            .select().from(CHAT)
            .fetch(CHAT.ID)
            .stream()
            .toList();
    }

    @Override
    public boolean isPresent(long chatId) {
        long count = dslContext
            .select().from(CHAT)
            .where(CHAT.ID.eq(chatId))
            .stream()
            .count();
        return count == 1;
    }

}
