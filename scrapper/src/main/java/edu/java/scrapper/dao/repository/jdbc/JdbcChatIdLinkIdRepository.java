package edu.java.scrapper.dao.repository.jdbc;

import edu.java.scrapper.dao.repository.dto.ChatIdLinkId;
import edu.java.scrapper.dao.repository.dto.mappers.ChatIdLinkIdRowMapper;
import edu.java.scrapper.dao.repository.interfaces.ChatIdLinkIdRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcChatIdLinkIdRepository implements ChatIdLinkIdRepository {

    private final static String ADD_QUERY = "INSERT INTO ChatIdLinkId (chat_id, link_id) VALUES (?, ?)";
    private final static String REMOVE_QUERY = "DELETE FROM ChatIdLinkId WHERE chat_id = ? AND link_id = ?";
    private final static String FIND_ALL_QUERY = "SELECT * FROM ChatIdLinkId";
    private final static String FIND_ALL_BY_CHAT_ID_QUERY = "SELECT * FROM ChatIdLinkId WHERE chat_id = ?";
    private final static RowMapper<ChatIdLinkId> ROW_MAPPER = new ChatIdLinkIdRowMapper();
    private final JdbcClient jdbcClient;

    @Autowired
    public JdbcChatIdLinkIdRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void add(long chatId, long linkId) {
        jdbcClient
            .sql(ADD_QUERY)
            .param(chatId)
            .param(linkId)
            .update();
    }

    @Override
    public void remove(long chatId, long linkId) {
        jdbcClient
            .sql(REMOVE_QUERY)
            .param(chatId)
            .param(linkId)
            .update();
    }

    @Override
    public List<ChatIdLinkId> findAll() {
        return jdbcClient
            .sql(FIND_ALL_QUERY)
            .query(ROW_MAPPER)
            .list();
    }

    @Override
    public List<ChatIdLinkId> findAllByChatId(long chatId) {
        return jdbcClient
            .sql(FIND_ALL_BY_CHAT_ID_QUERY)
            .param(chatId)
            .query(ROW_MAPPER)
            .list();
    }

}
