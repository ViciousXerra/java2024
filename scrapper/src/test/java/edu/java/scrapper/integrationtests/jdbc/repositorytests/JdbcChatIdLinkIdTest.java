package edu.java.scrapper.integrationtests.jdbc.repositorytests;

import edu.java.scrapper.api.exceptions.UnhandledException;
import edu.java.scrapper.dao.dto.ChatIdLinkId;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.dto.mappers.ChatIdLinkIdRowMapper;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatIdLinkIdRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
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

class JdbcChatIdLinkIdTest extends IntegrationTest {
    private final static RowMapper<ChatIdLinkId> ROW_MAPPER = new ChatIdLinkIdRowMapper();
    public static final String INSERT_QUERY1 = "INSERT INTO ChatIdLinkId (chat_id, link_id) VALUES (?, ?)";
    public static final String INSERT_QUERY2 = "INSERT INTO ChatIdLinkId (chat_id, link_id) VALUES (?, ?), (?, ?)";

    @Autowired
    private JdbcChatIdLinkIdRepository chatIdLinkIdRepository;
    @Autowired
    private JdbcChatRepository chatRepository;
    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcClient jdbcClient;

    @Test
    @DisplayName("Test \"add\" chatIdLinkId repository method")
    @Transactional
    @Rollback
    void addChatIdLinkIdTest() {
        //Given
        long expectedChatId = 1L;
        chatRepository.add(expectedChatId);
        Link expectedLink = linkRepository.add("link1");
        long expectedLinkId = expectedLink.linkId();
        //When
        chatIdLinkIdRepository.add(expectedChatId, expectedLinkId);
        ChatIdLinkId actualChatIdLinkId = jdbcClient.sql("SELECT * FROM ChatIdLinkId").query(ROW_MAPPER).single();
        Assertions.assertAll(
            () -> assertThat(actualChatIdLinkId.chatId()).isEqualTo(expectedChatId),
            () -> assertThat(actualChatIdLinkId.linkId()).isEqualTo(expectedLinkId)
        );
    }

    @Test
    @DisplayName("Test \"remove\" chatIdLinkId repository method")
    @Transactional
    @Rollback
    void removeChatIdLinkIdTest() {
        //Given
        long expectedChatId = 1L;
        chatRepository.add(expectedChatId);
        Link expectedLink = linkRepository.add("link1");
        long expectedLinkId = expectedLink.linkId();
        //When
        int update = jdbcClient.sql(INSERT_QUERY1)
            .param(expectedChatId).param(expectedLinkId).update();
        chatIdLinkIdRepository.remove(expectedChatId, expectedLinkId);
        List<ChatIdLinkId> actualChatIdLinkIdList =
            jdbcClient.sql("SELECT * FROM ChatIdLinkId").query(ROW_MAPPER).list();
        //Then
        Assertions.assertAll(
            () -> assertThat(update).isNotZero(),
            () -> assertThat(actualChatIdLinkIdList).isEmpty()
        );
    }

    @Test
    @DisplayName("Test \"findAll\" chatIdLinkId repository method")
    @Transactional
    @Rollback
    void findAllChatIdLinkIdTest() {
        //Given
        long expectedChatId1 = 1L;
        long expectedChatId2 = 2L;
        chatRepository.add(expectedChatId1);
        chatRepository.add(expectedChatId2);
        Link expectedLink1 = linkRepository.add("link1");
        Link expectedLink2 = linkRepository.add("link2");
        long expectedLinkId1 = expectedLink1.linkId();
        long expectedLinkId2 = expectedLink2.linkId();
        List<ChatIdLinkId> expectedList = List.of(
            new ChatIdLinkId(expectedChatId1, expectedLinkId1),
            new ChatIdLinkId(expectedChatId2, expectedLinkId2)
        );
        //When
        int update = jdbcClient.sql(INSERT_QUERY2)
            .param(expectedChatId1).param(expectedLinkId1).param(expectedChatId2).param(expectedLinkId2).update();
        List<ChatIdLinkId> actualChatIdLinkIdList = chatIdLinkIdRepository.findAll();
        //Then
        Assertions.assertAll(
            () -> assertThat(update).isNotZero(),
            () -> assertThat(actualChatIdLinkIdList).containsOnlyOnceElementsOf(expectedList)
        );
    }

    @Test
    @DisplayName("Test \"findAllByChatId\" chatIdLinkId repository method")
    @Transactional
    @Rollback
    void findAllByChatIdChatIdLinkIdTest() {
        //Given
        long expectedChatId1 = 1L;
        long expectedChatId2 = 2L;
        chatRepository.add(expectedChatId1);
        chatRepository.add(expectedChatId2);
        Link expectedLink1 = linkRepository.add("link1");
        Link expectedLink2 = linkRepository.add("link2");
        long expectedLinkId1 = expectedLink1.linkId();
        long expectedLinkId2 = expectedLink2.linkId();
        List<ChatIdLinkId> expectedList = List.of(
            new ChatIdLinkId(expectedChatId1, expectedLinkId1)
        );
        //When
        int update = jdbcClient.sql(INSERT_QUERY2)
            .param(expectedChatId1).param(expectedLinkId1).param(expectedChatId2).param(expectedLinkId2).update();
        List<ChatIdLinkId> actualChatIdLinkIdList = chatIdLinkIdRepository.findAllByChatId(expectedChatId1);
        //Then
        Assertions.assertAll(
            () -> assertThat(update).isNotZero(),
            () -> assertThat(actualChatIdLinkIdList).containsOnlyOnceElementsOf(expectedList)
        );
    }

    @Test
    @DisplayName("Test \"findAllByLinkId\" chatIdLinkId repository method")
    @Transactional
    @Rollback
    void findAllByLinkIdChatIdLinkIdTest() {
        //Given
        long expectedChatId1 = 1L;
        long expectedChatId2 = 2L;
        chatRepository.add(expectedChatId1);
        chatRepository.add(expectedChatId2);
        Link expectedLink1 = linkRepository.add("link1");
        Link expectedLink2 = linkRepository.add("link2");
        long expectedLinkId1 = expectedLink1.linkId();
        long expectedLinkId2 = expectedLink2.linkId();
        List<ChatIdLinkId> expectedList = List.of(
            new ChatIdLinkId(expectedChatId2, expectedLinkId2)
        );
        //When
        int update = jdbcClient.sql(INSERT_QUERY2)
            .param(expectedChatId1).param(expectedLinkId1).param(expectedChatId2).param(expectedLinkId2).update();
        List<ChatIdLinkId> actualChatIdLinkIdList = chatIdLinkIdRepository.findAllByLinkId(expectedLinkId2);
        //Then
        Assertions.assertAll(
            () -> assertThat(update).isNotZero(),
            () -> assertThat(actualChatIdLinkIdList).containsOnlyOnceElementsOf(expectedList)
        );
    }

    @Test
    @DisplayName("Test \"add\" constraints violation repository method")
    @Transactional
    @Rollback
    void addConstraintsByIdChatIdLinkIdTest() {
        //Given
        long expectedChatId = 1L;
        chatRepository.add(expectedChatId);
        Link expectedLink = linkRepository.add("link1");
        long expectedLinkId = expectedLink.linkId();
        //When
        assertThatThrownBy(() -> {
            chatIdLinkIdRepository.add(expectedChatId, expectedLinkId);
            chatIdLinkIdRepository.add(expectedChatId, expectedLinkId);
        })
            .isInstanceOf(UnhandledException.class)
            .satisfies(exception ->
                Assertions.assertAll(
                    () -> assertThat(exception.getMessage()).isEqualTo("Reference table constraints violation"),
                    () -> assertThat(((UnhandledException) exception).getDescription()).isEqualTo(
                        "Unable to insert data. Constraints violation is presented")
                )
            );
    }

    @Test
    @DisplayName("Test \"remove\" constraints violation repository method")
    @Transactional
    @Rollback
    void removeConstraintsByIdChatIdLinkIdTest() {
        assertThatThrownBy(() -> chatIdLinkIdRepository.remove(1, 1))
            .isInstanceOf(UnhandledException.class)
            .satisfies(exception ->
                Assertions.assertAll(
                    () -> assertThat(exception.getMessage()).isEqualTo("Row hasn't been founded in reference table"),
                    () -> assertThat(((UnhandledException) exception).getDescription()).isEqualTo(
                        "Unable to delete data. Data is not presented")
                )
            );
    }

    @Test
    @DisplayName("Test cascade deletion chatIdLinkId repository")
    @Transactional
    @Rollback
    void cascadeDeletionTest() {
        //Given
        long expectedChatId1 = 1L;
        long expectedChatId2 = 2L;
        chatRepository.add(expectedChatId1);
        chatRepository.add(expectedChatId2);
        Link expectedLink1 = linkRepository.add("link1");
        Link expectedLink2 = linkRepository.add("link2");
        long expectedLinkId1 = expectedLink1.linkId();
        long expectedLinkId2 = expectedLink2.linkId();
        //When
        int update1 = jdbcClient.sql(INSERT_QUERY2)
            .param(expectedChatId1).param(expectedLinkId1).param(expectedChatId2).param(expectedLinkId2).update();
        chatRepository.remove(expectedChatId1);
        chatRepository.remove(expectedChatId2);
        List<ChatIdLinkId> actualChatIdLinkIdList1 = chatIdLinkIdRepository.findAll();
        //Then
        Assertions.assertAll(
            () -> assertThat(update1).isNotZero(),
            () -> assertThat(actualChatIdLinkIdList1).isEmpty()
        );
        //When
        chatRepository.add(expectedChatId1);
        chatRepository.add(expectedChatId2);
        int update2 = jdbcClient.sql(INSERT_QUERY2)
            .param(expectedChatId1).param(expectedLinkId1).param(expectedChatId2).param(expectedLinkId2).update();
        linkRepository.remove("link1");
        linkRepository.remove("link2");
        List<ChatIdLinkId> actualChatIdLinkIdList2 = chatIdLinkIdRepository.findAll();
        //Then
        Assertions.assertAll(
            () -> assertThat(update2).isNotZero(),
            () -> assertThat(actualChatIdLinkIdList2).isEmpty()
        );
    }

}
