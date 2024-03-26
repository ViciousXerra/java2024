package edu.java.scrapper.integrationtests;

import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.dto.mappers.ChatIdLinkIdRowMapper;
import edu.java.scrapper.dao.dto.mappers.LinkRowMapper;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LiquibaseSampleTest extends IntegrationTest {

    private static final String INSERT_CHAT_ID_VALUES_QUERY = "INSERT INTO Chat (id) VALUES (1), (2), (3)";
    private static final String INSERT_LINK_VALUES_QUERY =
        "INSERT INTO Link (url) VALUES ('link1'), ('link2'), ('link3')";
    private static final String INSERT_CHAT_ID_LINK_ID_QUERY =
        "INSERT INTO ChatIdLinkId (chat_id, link_id) VALUES (?, ?)";
    private static final String RETURNING_APPEND = " RETURNING *";
    private static final String ID_COLUMN_NAME = "id";
    private static final String URL_COLUMN_NAME = "url";
    private static final RowMapper<Long> ID_ROW_MAPPER = (rs, rowCol) -> rs.getLong(ID_COLUMN_NAME);
    private static final RowMapper<String> URL_ROW_MAPPER = (rs, rowCol) -> rs.getString(URL_COLUMN_NAME);
    private static final LinkRowMapper LINK_MAPPER = new LinkRowMapper();
    private static final ChatIdLinkIdRowMapper CHAT_ID_LINK_ID_ROW_MAPPER = new ChatIdLinkIdRowMapper();

    @Autowired
    private JdbcClient jdbcClient;

    @DynamicPropertySource
    static void stubDatabaseAccessType(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jdbc");
    }

    @Test
    @DisplayName("Test successful insert into Chat")
    @Transactional
    @Rollback
    void insertChatIdTest() {
        //Given
        List<Long> expectedIds = List.of(1L, 2L, 3L);
        //When
        List<Long> actualIds =
            jdbcClient.sql(INSERT_CHAT_ID_VALUES_QUERY + RETURNING_APPEND).query(ID_ROW_MAPPER).list();
        //Then
        assertThat(actualIds).containsOnlyOnceElementsOf(expectedIds);
    }

    @Test
    @DisplayName("Test successful delete from Chat")
    @Transactional
    @Rollback
    void deleteChatIdTest() {
        //Given
        List<Long> expectedIds = List.of(1L, 3L);
        //When
        jdbcClient.sql(INSERT_CHAT_ID_VALUES_QUERY).update();
        jdbcClient.sql("DELETE FROM Chat WHERE id = ?").param(2L).update();
        List<Long> actualIds = jdbcClient.sql("SELECT id FROM Chat").query(ID_ROW_MAPPER).list();
        //Then
        assertThat(actualIds).containsOnlyOnceElementsOf(expectedIds);
    }

    @Test
    @DisplayName("Test constraints violation for Chat")
    @Transactional
    @Rollback
    void insertConstraintsViolationForChatId() {
        //Then
        assertThatThrownBy(() -> {
            jdbcClient.sql(INSERT_CHAT_ID_VALUES_QUERY).update();
            jdbcClient.sql(INSERT_CHAT_ID_VALUES_QUERY).update();
        })
            .isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("Test successful insert in Link")
    @Transactional
    @Rollback
    void insertLinkTest() {
        //Given
        List<String> expectedUrls = List.of("link1", "link2", "link3");
        //When
        List<Link> actualLinks =
            jdbcClient.sql(INSERT_LINK_VALUES_QUERY + RETURNING_APPEND).query(LINK_MAPPER).list();
        //Then
        Assertions.assertAll(
            () -> assertThat(actualLinks.stream().map(Link::url)).containsOnlyOnceElementsOf(expectedUrls),
            () -> assertThat(actualLinks.stream().map(Link::linkId)).doesNotHaveDuplicates()
        );

    }

    @Test
    @DisplayName("Test successful delete from Link")
    @Transactional
    @Rollback
    void deleteLinkTest() {
        //Given
        List<String> expectedUrls = List.of("link1", "link3");
        //When
        jdbcClient.sql(INSERT_LINK_VALUES_QUERY).update();
        jdbcClient.sql("DELETE FROM Link WHERE url = ?").param("link2").update();
        List<String> actualUrls = jdbcClient.sql("SELECT url FROM Link").query(URL_ROW_MAPPER).list();
        //Then
        assertThat(actualUrls).containsOnlyOnceElementsOf(expectedUrls);
    }

    @Test
    @DisplayName("Test constraints violation for URL in Link")
    @Transactional
    @Rollback
    void insertConstraintsViolationForUrl() {
        //Then
        assertThatThrownBy(() -> {
            jdbcClient.sql(INSERT_LINK_VALUES_QUERY).update();
            jdbcClient.sql(INSERT_LINK_VALUES_QUERY).update();
        }).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @DisplayName("Test constraints violation for id in Link")
    @Transactional
    @Rollback
    void insertConstraintsViolationForId() {
        //Then
        assertThatThrownBy(() -> {
            Link link =
                jdbcClient.sql(INSERT_LINK_VALUES_QUERY + RETURNING_APPEND).query(LINK_MAPPER).list().getFirst();
            //id must be generated! Not inserted!
            jdbcClient.sql("INSERT INTO Link (id, url) VALUES (?, ?)").params(link.linkId(), "another_link").update();
        }).isInstanceOf(BadSqlGrammarException.class);
    }

    @Test
    @DisplayName("Test successful insert into ChatIdLinkId")
    @Transactional
    @Rollback
    void addChatIdLinkIdTest() {
        //Given chat_id
        long expectedChatId = 1L;
        jdbcClient.sql(INSERT_CHAT_ID_VALUES_QUERY).update();
        Link expectedLink =
            jdbcClient.sql(INSERT_LINK_VALUES_QUERY + RETURNING_APPEND).query(LINK_MAPPER).list().getFirst();
        //Given link_id
        long expectedLinkId = expectedLink.linkId();
        //When
        jdbcClient.sql(INSERT_CHAT_ID_LINK_ID_QUERY)
            .params(expectedChatId, expectedLink.linkId()).update();
        //Then
        ChatIdLinkId actualChatIdLinkId =
            jdbcClient.sql("SELECT * FROM ChatIdLinkId").query(CHAT_ID_LINK_ID_ROW_MAPPER).single();
        Assertions.assertAll(
            () -> assertThat(actualChatIdLinkId.chatId()).isEqualTo(expectedChatId),
            () -> assertThat(actualChatIdLinkId.linkId()).isEqualTo(expectedLinkId),
            () -> assertThatThrownBy(() -> jdbcClient.sql(INSERT_CHAT_ID_LINK_ID_QUERY)
                .params(expectedChatId, expectedLink.linkId()).update()).isInstanceOf(PreparedStatementCallback.class)
        );
    }

    @Test
    @DisplayName("Test ChatIdLinkId constraints violation")
    @Transactional
    @Rollback
    void testChatIdLinkIdConstraintsViolation() {
        //Then
        assertThatThrownBy(() -> jdbcClient.sql(INSERT_CHAT_ID_LINK_ID_QUERY)
            .params(1L, 2L).update()).isInstanceOf(DataIntegrityViolationException.class);
    }

}
