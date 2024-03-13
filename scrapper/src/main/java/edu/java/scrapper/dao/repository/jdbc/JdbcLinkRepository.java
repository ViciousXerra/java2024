package edu.java.scrapper.dao.repository.jdbc;

import edu.java.scrapper.api.exceptions.UnhandledException;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.dto.mappers.LinkRowMapper;
import edu.java.scrapper.dao.repository.interfaces.LinkRepository;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcLinkRepository implements LinkRepository {

    private final static String ADD_QUERY = "INSERT INTO Link (url) VALUES (?) RETURNING *";
    private final static String REMOVE_QUERY = "DELETE FROM Link WHERE url = ? RETURNING *";
    private final static String FIND_ALL_QUERY = "SELECT * FROM Link";
    private final static String FIND_UP_TO_CHECK_QUERY = "SELECT * FROM Link ORDER BY checked_at LIMIT ?";
    private final static String MODIFY_UPDATED_AT_QUERY =
        "UPDATE Link SET checked_at = ?, updated_at = ? WHERE url = ?";
    private final static String MODIFY_CHECKED_AT_QUERY =
        "UPDATE Link SET checked_at = ? WHERE url = ?";

    private final static RowMapper<Link> ROW_MAPPER = new LinkRowMapper();
    private final JdbcClient jdbcClient;

    @Autowired
    public JdbcLinkRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Link add(String url) {
        try {
            return jdbcClient
                .sql(ADD_QUERY)
                .param(url)
                .query(ROW_MAPPER)
                .single();
        } catch (DuplicateKeyException e) {
            throw new UnhandledException("URL must be unique", "Unable to insert url data");
        }
    }

    @Override
    public Link remove(String url) {
        try {
            return jdbcClient
                .sql(REMOVE_QUERY)
                .param(url)
                .query(ROW_MAPPER)
                .single();
        } catch (EmptyResultDataAccessException e) {
            throw new UnhandledException("URL hasn't been founded", "Unable to delete url data");
        }
    }

    @Override
    public List<Link> findAll() {
        return jdbcClient
            .sql(FIND_ALL_QUERY)
            .query(ROW_MAPPER)
            .list();
    }

    @Override
    public List<Link> findUpToCheck(int limit) {
        return jdbcClient
            .sql(FIND_UP_TO_CHECK_QUERY)
            .param(limit)
            .query(ROW_MAPPER)
            .list();
    }

    @Override
    public void modifyUpdatedAtTimestamp(String url, ZonedDateTime newCheckedAt, ZonedDateTime newUpdatedAt) {
        jdbcClient
            .sql(MODIFY_UPDATED_AT_QUERY)
            .param(Timestamp.from(newCheckedAt.toInstant()))
            .param(Timestamp.from(newUpdatedAt.toInstant()))
            .param(url)
            .update();
    }

    @Override
    public void modifyCheckedAtTimestamp(String url, ZonedDateTime newCheckedAt) {
        jdbcClient
            .sql(MODIFY_CHECKED_AT_QUERY)
            .param(Timestamp.from(newCheckedAt.toInstant()))
            .param(url)
            .update();
    }

}
