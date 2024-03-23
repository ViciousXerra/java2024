package edu.java.scrapper.integrationtests.jooq.servicestests;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.dto.mappers.ChatIdLinkIdRowMapper;
import edu.java.scrapper.dao.dto.mappers.LinkRowMapper;
import edu.java.scrapper.dao.service.jooq.JooqLinkService;
import edu.java.scrapper.integrationtests.IntegrationTest;
import java.util.Collection;
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

public class JooqLinkServiceTest extends IntegrationTest {

    private static final String INSERT_INTO_CHAT_QUERY1 = "INSERT INTO Chat (id) VALUES (?), (?)";
    private static final String INSERT_INTO_CHAT_QUERY2 = "INSERT INTO Chat (id) VALUES (?)";
    private static final String INSERT_INTO_LINK_QUERY = "INSERT INTO Link (url) VALUES (?), (?) RETURNING *";
    private static final String INSERT_INTO_CHATID_LINK_ID_QUERY1 =
        "INSERT INTO ChatIdLinkId (chat_id, link_id) VALUES (?, ?), (?, ?), (?, ?)";
    private static final String INSERT_INTO_CHATID_LINKID_QUERY2 =
        "INSERT INTO ChatIdLinkId (chat_id, link_id) VALUES (?, ?), (?, ?)";
    private static final String SELECT_FROM_CHATID_LINKID_QUERY = "SELECT * FROM ChatIdLinkId";
    private static final String SELECT_FROM_LINK_QUERY = "SELECT * FROM Link";
    @Autowired
    private JooqLinkService jdbcLinkService;
    @Autowired
    private JdbcClient jdbcClient;
    private final static RowMapper<Link> LINK_ROW_MAPPER = new LinkRowMapper();
    private final static RowMapper<ChatIdLinkId> CHAT_ID_LINK_ID_ROW_MAPPER = new ChatIdLinkIdRowMapper();

    @Test
    @DisplayName("Test unauthorized link management")
    @Transactional
    @Rollback
    void testUnauthorizedLinkManagement() {
        Assertions.assertAll(
            () -> assertThatThrownBy(
                () -> jdbcLinkService.add(1L, "link1")).isInstanceOf(NotFoundException.class)
                .hasMessage("Links not found.")
                .satisfies(exception -> assertThat(((NotFoundException) exception).getDescription()).isEqualTo(
                        "Registration required for managing links for tracking."
                    )
                ),
            () -> assertThatThrownBy(
                () -> jdbcLinkService.remove(1L, "link1")).isInstanceOf(NotFoundException.class)
                .hasMessage("Links not found.")
                .satisfies(exception -> assertThat(((NotFoundException) exception).getDescription()).isEqualTo(
                        "Registration required for managing links for tracking."
                    )
                )
        );
    }

    @Test
    @DisplayName("Test successful link registration")
    @Transactional
    @Rollback
    void testSuccessfulLinkRegistration() {
        //Given
        long expectedChatId1 = 1L;
        long expectedChatId2 = 2L;
        int update =
            jdbcClient.sql(INSERT_INTO_CHAT_QUERY1).params(expectedChatId1, expectedChatId2).update();
        //When
        Link returnedLink1 = jdbcLinkService.add(expectedChatId1, "link1");
        Link returnedLink2 = jdbcLinkService.add(expectedChatId1, "link2");
        Link returnedLink3 = jdbcLinkService.add(expectedChatId2, "link2");
        List<Link> expectedLinksList = List.of(returnedLink1, returnedLink2, returnedLink3);

        List<ChatIdLinkId> expectedChatIdLinkIdList = List.of(
            new ChatIdLinkId(expectedChatId1, returnedLink1.linkId()),
            new ChatIdLinkId(expectedChatId1, returnedLink2.linkId()),
            new ChatIdLinkId(expectedChatId2, returnedLink3.linkId())
        );
        List<ChatIdLinkId> actualChatIdLinkIdList =
            jdbcClient.sql(SELECT_FROM_CHATID_LINKID_QUERY).query(CHAT_ID_LINK_ID_ROW_MAPPER).list();
        List<Link> actualLinksList =
            jdbcClient.sql(SELECT_FROM_LINK_QUERY).query(LINK_ROW_MAPPER).list();
        //Then
        Assertions.assertAll(
            () -> assertThat(update).isNotZero(),
            () -> assertThat(actualChatIdLinkIdList).containsOnlyOnceElementsOf(expectedChatIdLinkIdList),
            () -> assertThat(actualLinksList).containsOnlyOnceElementsOf(expectedLinksList)
        );
    }

    @Test
    @DisplayName("Test link registration violation")
    @Transactional
    @Rollback
    void testLinkRegistrationViolation() {
        //Given
        long expectedChatId = 1L;
        int update = jdbcClient.sql(INSERT_INTO_CHAT_QUERY2).param(expectedChatId).update();
        //Then
        Assertions.assertAll(
            () -> assertThat(update).isNotZero(),
            () -> assertThatThrownBy(
                () -> {
                    jdbcLinkService.add(1L, "link1");
                    jdbcLinkService.add(1L, "link1");
                }).isInstanceOf(ConflictException.class)
                .hasMessage("Unable to insert url data.")
                .satisfies(exception -> assertThat(((ConflictException) exception).getDescription()).isEqualTo(
                        "URL must be unique."
                    )
                )
        );
    }

    @Test
    @DisplayName("Test successful link deletion")
    @Transactional
    @Rollback
    void testSuccessfulLinkDeletion() {
        //Given
        long expectedChatId1 = 1L;
        long expectedChatId2 = 2L;
        int update1 =
            jdbcClient.sql(INSERT_INTO_CHAT_QUERY1).params(expectedChatId1, expectedChatId2).update();
        List<Link> returnedLinksList =
            jdbcClient.sql(INSERT_INTO_LINK_QUERY).params("link1", "link2")
                .query(LINK_ROW_MAPPER).list();
        int update2 = jdbcClient.sql(INSERT_INTO_CHATID_LINK_ID_QUERY1)
            .params(
                expectedChatId1, returnedLinksList.getFirst().linkId(),
                expectedChatId1, returnedLinksList.getLast().linkId(),
                expectedChatId2, returnedLinksList.getLast().linkId()
            ).update();
        //When
        List<ChatIdLinkId> expectedRelationsList1 = List.of(
            new ChatIdLinkId(expectedChatId1, returnedLinksList.getFirst().linkId()),
            new ChatIdLinkId(expectedChatId1, returnedLinksList.getLast().linkId())
        );
        List<Link> expectedLinksList1 = List.of(returnedLinksList.getFirst(), returnedLinksList.getLast());
        jdbcLinkService.remove(expectedChatId2, "link2");
        List<ChatIdLinkId> actualRelationsList1 =
            jdbcClient.sql(SELECT_FROM_CHATID_LINKID_QUERY).query(CHAT_ID_LINK_ID_ROW_MAPPER).list();
        List<Link> actualLinksList1 =
            jdbcClient.sql(SELECT_FROM_LINK_QUERY).query(LINK_ROW_MAPPER).list();
        List<ChatIdLinkId> expectedRelationsList2 = List.of(
            new ChatIdLinkId(expectedChatId1, returnedLinksList.getFirst().linkId())
        );
        List<Link> expectedLinksList2 = List.of(returnedLinksList.getFirst());
        jdbcLinkService.remove(expectedChatId1, "link2");
        List<ChatIdLinkId> actualRelationsList2 =
            jdbcClient.sql(SELECT_FROM_CHATID_LINKID_QUERY).query(CHAT_ID_LINK_ID_ROW_MAPPER).list();
        List<Link> actualLinksList2 =
            jdbcClient.sql(SELECT_FROM_LINK_QUERY).query(LINK_ROW_MAPPER).list();
        List<ChatIdLinkId> expectedRelationsList3 = List.of();
        List<Link> expectedLinksList3 = List.of();
        jdbcLinkService.remove(expectedChatId1, "link1");
        List<ChatIdLinkId> actualRelationsList3 =
            jdbcClient.sql(SELECT_FROM_CHATID_LINKID_QUERY).query(CHAT_ID_LINK_ID_ROW_MAPPER).list();
        List<Link> actualLinksList3 =
            jdbcClient.sql(SELECT_FROM_LINK_QUERY).query(LINK_ROW_MAPPER).list();
        //Then
        Assertions.assertAll(
            () -> assertThat(update1).isNotZero(),
            () -> assertThat(update2).isNotZero(),
            () -> assertThat(actualRelationsList1).containsOnlyOnceElementsOf(expectedRelationsList1),
            () -> assertThat(actualRelationsList2).containsOnlyOnceElementsOf(expectedRelationsList2),
            () -> assertThat(actualRelationsList3).containsOnlyOnceElementsOf(expectedRelationsList3),
            () -> assertThat(actualLinksList1).containsOnlyOnceElementsOf(expectedLinksList1),
            () -> assertThat(actualLinksList2).containsOnlyOnceElementsOf(expectedLinksList2),
            () -> assertThat(actualLinksList3).containsOnlyOnceElementsOf(expectedLinksList3)
        );
    }

    @Test
    @DisplayName("Test link deletion violation")
    @Transactional
    @Rollback
    void testLinkDeletionViolation() {
        //Given
        long expectedChatId1 = 1L;
        long expectedChatId2 = 2L;
        int update1 =
            jdbcClient.sql(INSERT_INTO_CHAT_QUERY1).params(expectedChatId1, expectedChatId2).update();
        List<Link> returnedLinkList =
            jdbcClient.sql(INSERT_INTO_LINK_QUERY).params("link1", "link2")
                .query(LINK_ROW_MAPPER)
                .list();
        int update2 = jdbcClient.sql(INSERT_INTO_CHATID_LINKID_QUERY2)
            .params(
                expectedChatId1, returnedLinkList.getFirst().linkId(),
                expectedChatId2, returnedLinkList.getLast().linkId()
            ).update();
        //Then
        Assertions.assertAll(
            () -> assertThat(update1).isNotZero(),
            () -> assertThat(update2).isNotZero(),
            () -> assertThatThrownBy(
                () -> jdbcLinkService.remove(expectedChatId1, "notExistedLink")).isInstanceOf(NotFoundException.class)
                .hasMessage("Unable to delete url data.")
                .satisfies(exception -> assertThat(((NotFoundException) exception).getDescription()).isEqualTo(
                        "URL hasn't been registered."
                    )
                ),
            () -> assertThatThrownBy(
                () -> jdbcLinkService.remove(expectedChatId1, returnedLinkList.getLast().url())).isInstanceOf(
                    NotFoundException.class)
                .hasMessage("Unable to delete url data.")
                .satisfies(exception -> assertThat(((NotFoundException) exception).getDescription()).isEqualTo(
                        "URL hasn't been founded."
                    )
                )
        );
    }

    @Test
    @DisplayName("Test list all method")
    @Transactional
    @Rollback
    void testListAll() {
        //Given
        long expectedChatId1 = 1L;
        long expectedChatId2 = 2L;
        int update1 =
            jdbcClient.sql(INSERT_INTO_CHAT_QUERY1).params(expectedChatId1, expectedChatId2).update();
        List<Link> returnedLinksList =
            jdbcClient.sql(INSERT_INTO_LINK_QUERY).params("link1", "link2")
                .query(LINK_ROW_MAPPER).list();
        int update2 = jdbcClient.sql(INSERT_INTO_CHATID_LINK_ID_QUERY1)
            .params(
                expectedChatId1, returnedLinksList.getFirst().linkId(),
                expectedChatId1, returnedLinksList.getLast().linkId(),
                expectedChatId2, returnedLinksList.getLast().linkId()
            ).update();
        List<Link> expectedLinksList1 = List.of(returnedLinksList.getFirst(), returnedLinksList.getLast());
        List<Link> expectedLinksList2 = List.of(returnedLinksList.getLast());
        //When
        Collection<Link> actualLinksCollection1 = jdbcLinkService.listAll(expectedChatId1);
        Collection<Link> actualLinksCollection2 = jdbcLinkService.listAll(expectedChatId2);
        //Then
        Assertions.assertAll(
            () -> assertThat(update1).isNotZero(),
            () -> assertThat(update2).isNotZero(),
            () -> assertThat(actualLinksCollection1).asList().containsOnlyOnceElementsOf(expectedLinksList1),
            () -> assertThat(actualLinksCollection2).asList().containsOnlyOnceElementsOf(expectedLinksList2)
        );
    }

}
