package edu.java.scrapper.integrationtests.jdbc.servicestests;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.dto.mappers.ChatIdLinkIdRowMapper;
import edu.java.scrapper.dao.dto.mappers.LinkRowMapper;
import edu.java.scrapper.dao.service.jdbc.JdbcChatService;
import edu.java.scrapper.integrationtests.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JdbcChatServiceTest extends IntegrationTest {

    @Autowired
    private JdbcChatService jdbcChatService;
    @Autowired
    private JdbcClient jdbcClient;
    private final static RowMapper<Link> LINK_ROW_MAPPER = new LinkRowMapper();
    private final static RowMapper<ChatIdLinkId> CHAT_ID_LINK_ID_ROW_MAPPER = new ChatIdLinkIdRowMapper();

    @Test
    @DisplayName("Test successful registration")
    @Transactional
    @Rollback
    void testSuccessfulRegistration() {
        //Given
        long expectedChatId1 = 36L;
        long expectedChatId2 = 63L;
        List<Long> expectedChatIds = List.of(expectedChatId1, expectedChatId2);
        //When
        jdbcChatService.register(expectedChatId1);
        jdbcChatService.register(expectedChatId2);
        List<Long> actualChatIds = jdbcClient.sql("SELECT id FROM Chat").query((rs, rowCol) -> rs.getLong("id")).list();
        //Then
        assertThat(actualChatIds).containsOnlyOnceElementsOf(expectedChatIds);
    }

    @Test
    @DisplayName("Test successful deletion")
    @Transactional
    @Rollback
    void testSuccessfulDeletion() {
        //Given
        long expectedChatId1 = 36L;
        long expectedChatId2 = 63L;
        List<Long> expectedChatIds = List.of(expectedChatId2);
        //When
        int update =
            jdbcClient.sql("INSERT INTO Chat (id) VALUES (?), (?)").params(expectedChatId1, expectedChatId2).update();
        jdbcChatService.unregister(expectedChatId1);
        List<Long> actualChatIds = jdbcClient.sql("SELECT id FROM Chat").query((rs, rowCol) -> rs.getLong("id")).list();
        //Then
        Assertions.assertAll(
            () -> assertThat(update).isNotZero(),
            () -> assertThat(actualChatIds).containsOnlyOnceElementsOf(expectedChatIds)
        );
    }

    @Test
    @DisplayName("Test registration violation")
    @Transactional
    @Rollback
    void testRegistrationViolation() {
        //Then
        assertThatThrownBy(() -> {
            jdbcChatService.register(1L);
            jdbcChatService.register(1L);
        }).isInstanceOf(ConflictException.class)
            .hasMessage("Chat already signed up")
            .satisfies(exception -> assertThat(((ConflictException) exception).getDescription()).isEqualTo(
                "Chat associated with this id already signed up"));
    }

    @Test
    @DisplayName("Test deletion violation")
    @Transactional
    @Rollback
    void testDeletionViolation() {
        //Then
        assertThatThrownBy(() -> jdbcChatService.unregister(1L))
            .isInstanceOf(NotFoundException.class)
            .hasMessage("Chat not found")
            .satisfies(exception -> assertThat(((NotFoundException) exception).getDescription()).isEqualTo(
                "Chat associated with this id can't be founded"));
    }

    @Test
    @DisplayName("Test chain link deletion on chat deletion")
    @Transactional
    @Rollback
    void testChainLinkDeletionOnChatDeletion() {
        //Given
        long expectedChatId1 = 1L;
        long expectedChatId2 = 2L;
        String expectedLink1 = "link1";
        String expectedLink2 = "link2";
        int update1 =
            jdbcClient.sql("INSERT INTO Chat (id) VALUES (?), (?)").params(expectedChatId1, expectedChatId2).update();
        List<Link> links =
            jdbcClient.sql("INSERT INTO Link (url) VALUES (?), (?) RETURNING *").params(expectedLink1, expectedLink2)
                .query(LINK_ROW_MAPPER).list();
        int update2 = jdbcClient
            .sql("INSERT INTO ChatIdLinkId (chat_id, link_id) VALUES (?, ?), (?, ?), (?, ?)")
            .params(
                expectedChatId1, links.getFirst().linkId(),
                expectedChatId1, links.getLast().linkId(),
                expectedChatId2, links.getLast().linkId()
            ).update();
        List<Link> expectedLinks = List.of(links.getLast());
        List<ChatIdLinkId> expectedRelations = List.of(new ChatIdLinkId(2L, links.getLast().linkId()));
        //When
        jdbcChatService.unregister(expectedChatId1);
        List<Link> actualLinks = jdbcClient.sql("SELECT * FROM Link").query(LINK_ROW_MAPPER).list();
        List<ChatIdLinkId> actualRelations = jdbcClient.sql("SELECT * FROM ChatIdLinkId").query(CHAT_ID_LINK_ID_ROW_MAPPER).list();
        //Then
        Assertions.assertAll(
            () -> assertThat(update1).isNotZero(),
            () -> assertThat(update2).isNotZero(),
            () -> assertThat(actualLinks).containsOnlyOnceElementsOf(expectedLinks),
            () -> assertThat(actualRelations).containsOnlyOnceElementsOf(expectedRelations)
        );
    }

}
