package edu.java.scrapper;

import edu.java.scrapper.dao.repository.jdbc.JdbcChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

public class JdbcRepositoriesTest extends IntegrationTest {

    @Autowired
    private JdbcChatRepository chatRepository;
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcChatIdLinkIdRepository chatIdLinkIdRepository;
    private JdbcClient jdbcClient;

    @Autowired
    public JdbcRepositoriesTest(DataSource dataSource) {
        this.jdbcClient = JdbcClient.create(dataSource);
    }

    @Test
    @Transactional
    @Rollback
    void addChatIdTest() {
        //Given
        List<Long> expectedIds = List.of(1L, 2L, 3L);
        //When
        chatRepository.add(1L);
        chatRepository.add(2L);
        chatRepository.add(3L);
        List<Long> actualIds =
            jdbcClient.sql("SELECT id FROM Chat").query((rs, rowCol) -> Long.valueOf(rs.getString("id"))).list();
        //Then
        assertThat(actualIds).containsOnlyOnceElementsOf(expectedIds);
    }

    @Test
    @Transactional
    @Rollback
    void removeChatIdTest() {

    }

}
