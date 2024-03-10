package edu.java.scrapper.dao.repository.jdbc;

import edu.java.scrapper.dao.repository.interfaces.ChatRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcChatRepository implements ChatRepository {

    private final static String ADD_QUERY = "INSERT INTO Chat (id) VALUES (?)";
    private final static String REMOVE_QUERY = "DELETE FROM Chat WHERE id = ?";
    private final static String FIND_ALL_QUERY = "SELECT id FROM Chat";
    private final JdbcClient jdbcClient;

    @Autowired
    public JdbcChatRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void add(long chatId) {
        jdbcClient
            .sql(ADD_QUERY)
            .param(chatId)
            .update();
    }

    @Override
    public void remove(long chatId) {
        jdbcClient
            .sql(REMOVE_QUERY)
            .param(chatId)
            .update();
    }

    @Override
    public List<Long> findAll() {
        return jdbcClient
            .sql(FIND_ALL_QUERY)
            .query((rs, rowCol) -> Long.valueOf(rs.getString("id")))
            .list();
    }
}
