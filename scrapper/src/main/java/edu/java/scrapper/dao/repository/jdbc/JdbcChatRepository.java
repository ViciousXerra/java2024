package edu.java.scrapper.dao.repository.jdbc;

import edu.java.scrapper.api.exceptions.UnhandledException;
import edu.java.scrapper.dao.repository.interfaces.ChatRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcChatRepository implements ChatRepository {

    private final static String ADD_QUERY = "INSERT INTO Chat (id) VALUES (?)";
    private final static String REMOVE_QUERY = "DELETE FROM Chat WHERE id = ?";
    private final static String FIND_ALL_QUERY = "SELECT id FROM Chat";
    private final static String FIND_BY_ID_QUERY = "SELECT id FROM Chat WHERE id = ?";
    private final JdbcClient jdbcClient;

    @Autowired
    public JdbcChatRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void add(long chatId) {
        try {
            jdbcClient
                .sql(ADD_QUERY)
                .param(chatId)
                .update();
        } catch (DuplicateKeyException e) {
            throw new UnhandledException("Chat already signed up", "Chat associated with this id already signed up");
        }
    }

    @Override
    public void remove(long chatId) {
        int updates = jdbcClient
            .sql(REMOVE_QUERY)
            .param(chatId)
            .update();
        if (updates == 0) {
            throw new UnhandledException("Chat not found", "Chat associated with this id can't be founded");
        }
    }

    @Override
    public List<Long> findAll() {
        return jdbcClient
            .sql(FIND_ALL_QUERY)
            .query((rs, rowCol) -> Long.valueOf(rs.getString("id")))
            .list();
    }

    @Override
    public boolean isPresent(long chatId) {
        Optional<Long> chatOptional = jdbcClient
            .sql(FIND_BY_ID_QUERY)
            .param(chatId)
            .query((rs, rowCol) -> Long.valueOf(rs.getString("id")))
            .optional();
        return chatOptional.isPresent();
    }

}
