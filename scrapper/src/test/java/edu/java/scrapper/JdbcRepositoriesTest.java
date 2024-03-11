package edu.java.scrapper;

import edu.java.scrapper.dao.repository.jdbc.JdbcChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
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
    @Autowired
    private JdbcClient jdbcClient;

    @Test
    @DisplayName("Test \"add\" chat repository method")
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
    @DisplayName("Test \"remove\" chat repository method")
    @Transactional
    @Rollback
    void removeChatIdTest() {
        //Given
        List<Long> expectedIds = List.of(1L, 3L);
        //When
        jdbcClient.sql("INSERT INTO Chat (id) VALUES (1), (2), (3)").update();
        chatRepository.remove(2L);
        List<Long> actualIds =
            jdbcClient.sql("SELECT id FROM Chat").query((rs, rowCol) -> Long.valueOf(rs.getString("id"))).list();
        //Then
        assertThat(actualIds).containsOnlyOnceElementsOf(expectedIds);
    }

    @Test
    @DisplayName("Test \"find all\" chat repository method")
    @Transactional
    @Rollback
    void findAllChatIdTest() {
        //Given
        List<Long> expectedIds = List.of(1L, 2L, 3L, 4L, 5L);
        //When
        jdbcClient.sql("INSERT INTO Chat (id) VALUES (1), (2), (3), (4), (5)").update();
        List<Long> actualIds = chatRepository.findAll();
        //Then
        assertThat(actualIds).containsOnlyOnceElementsOf(expectedIds);
    }

}
