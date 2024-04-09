package edu.java.scrapper.dao.dto.mappers;

import edu.java.scrapper.dao.dto.Link;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.springframework.jdbc.core.RowMapper;

public class LinkRowMapper implements RowMapper<Link> {

    @Override
    public Link mapRow(ResultSet rs, int rowNum) throws SQLException {
        long linkId = rs.getLong("id");
        String url = rs.getString("url");
        ZonedDateTime updatedAt = rs.getTimestamp("updated_at").toInstant().atZone(ZoneOffset.UTC);
        ZonedDateTime checkedAt = rs.getTimestamp("checked_at").toInstant().atZone(ZoneOffset.UTC);
        return new Link(linkId, url, updatedAt, checkedAt);
    }

}
