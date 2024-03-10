package edu.java.scrapper.dao.repository.dto.mappers;

import edu.java.scrapper.dao.repository.dto.ChatIdLinkId;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChatIdLinkIdRowMapper implements RowMapper<ChatIdLinkId> {

    @Override
    public ChatIdLinkId mapRow(ResultSet rs, int rowNum) throws SQLException {
        long chatId = rs.getLong("chat_id");
        long linkId = rs.getLong("link_id");
        return new ChatIdLinkId(chatId, linkId);
    }

}
