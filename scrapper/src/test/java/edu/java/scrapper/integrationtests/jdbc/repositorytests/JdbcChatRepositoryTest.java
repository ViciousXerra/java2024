package edu.java.scrapper.integrationtests.jdbc.repositorytests;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.dao.repository.jdbc.JdbcChatRepository;
import edu.java.scrapper.integrationtests.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JdbcChatRepositoryTest extends IntegrationTest {

    @Autowired
    private JdbcChatRepository chatRepository;
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

    @Test
    @DisplayName("Test duplicate inserting")
    @Transactional
    @Rollback
    void testDuplicateInserting() {
        assertThatThrownBy(() -> {
            chatRepository.add(1L);
            chatRepository.add(1L);
        })
            .isInstanceOf(ConflictException.class)
            .satisfies(exception ->
                Assertions.assertAll(
                    () -> assertThat(exception.getMessage()).isEqualTo("Chat already signed up"),
                    () -> assertThat(((ConflictException) exception).getDescription()).isEqualTo(
                        "Chat associated with this id already signed up")
                )
            );
    }

    @Test
    @DisplayName("Test remove not existing id")
    @Transactional
    @Rollback
    void testRemoveNotExistingId() {
        assertThatThrownBy(() -> chatRepository.remove(1L))
            .isInstanceOf(NotFoundException.class)
            .satisfies(exception ->
                Assertions.assertAll(
                    () -> assertThat(exception.getMessage()).isEqualTo("Chat not found"),
                    () -> assertThat(((NotFoundException) exception).getDescription()).isEqualTo(
                        "Chat associated with this id can't be founded")
                )
            );
    }

}
