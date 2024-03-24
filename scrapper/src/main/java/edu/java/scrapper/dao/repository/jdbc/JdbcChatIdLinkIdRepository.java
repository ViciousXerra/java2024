package edu.java.scrapper.dao.repository.jdbc;

import edu.java.scrapper.api.exceptions.UnhandledException;
import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.mappers.ChatIdLinkIdRowMapper;
import edu.java.scrapper.dao.repository.interfaces.ChatIdLinkIdRepository;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;

public class JdbcChatIdLinkIdRepository implements ChatIdLinkIdRepository {

    private final static String ADD_QUERY = "INSERT INTO ChatIdLinkId (chat_id, link_id) VALUES (?, ?)";
    private final static String REMOVE_QUERY = "DELETE FROM ChatIdLinkId WHERE chat_id = ? AND link_id = ?";
    private final static String FIND_ALL_QUERY = "SELECT * FROM ChatIdLinkId";
    private final static String FIND_ALL_BY_CHAT_ID_QUERY = "SELECT * FROM ChatIdLinkId WHERE chat_id = ?";
    private final static String FIND_ALL_BY_LINK_ID_QUERY = "SELECT * FROM ChatIdLinkId WHERE link_id = ?";
    private final static RowMapper<ChatIdLinkId> ROW_MAPPER = new ChatIdLinkIdRowMapper();
    private final JdbcClient jdbcClient;

    public JdbcChatIdLinkIdRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void add(long chatId, long linkId) {
        try {
            jdbcClient
                .sql(ADD_QUERY)
                .param(chatId)
                .param(linkId)
                .update();
        } catch (DuplicateKeyException e) {
            throw new UnhandledException(
                "Reference table constraints violation.",
                "Unable to insert data. Constraints violation is presented."
            );
        }
    }

    @Override
    public void remove(long chatId, long linkId) {
        int updates = jdbcClient
            .sql(REMOVE_QUERY)
            .param(chatId)
            .param(linkId)
            .update();
        if (updates == 0) {
            throw new UnhandledException(
                "Row hasn't been founded in reference table.",
                "Unable to delete data. Data is not presented."
            );
        }
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

    @Override
    public List<ChatIdLinkId> findAllByLinkId(long linkId) {
        return jdbcClient
            .sql(FIND_ALL_BY_LINK_ID_QUERY)
            .param(linkId)
            .query(ROW_MAPPER)
            .list();
    }

}
