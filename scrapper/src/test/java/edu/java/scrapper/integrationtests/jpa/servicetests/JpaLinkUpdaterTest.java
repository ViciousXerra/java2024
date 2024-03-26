package edu.java.scrapper.integrationtests.jpa.servicetests;

import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.service.jpa.JpaLinkUpdater;
import edu.java.scrapper.integrationtests.jpa.JpaIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

class JpaLinkUpdaterTest extends JpaIntegrationTest {

    @Autowired
    private JpaLinkUpdater jpaLinkUpdater;
    @Autowired
    private JdbcClient jdbcClient;

    @DynamicPropertySource
    static void stubFetchLimit(DynamicPropertyRegistry registry) {
        registry.add("app.scheduler.fetch-limit", () -> 2);
    }

    @Test
    @DisplayName("Test fetching a link that has not been checked for a some time")
    @Transactional
    @Rollback
    void testLinkUpdater() {
        //Given
        int update = jdbcClient.sql(
            """
                INSERT INTO Link (url, checked_at) VALUES
                ('link1', '2004-10-18 11:23:54+03'),
                ('link2', '2004-10-17 11:23:54+03'),
                ('link3', '2004-10-16 11:23:54+03'),
                ('link4', '2004-10-19 11:23:54+03')
                """).update();
        List<String> expectedLinkList1 = List.of("link2", "link3");
        List<String> expectedLinkList2 = List.of("link1", "link4");
        //When
        List<String> actualLinkList1 = jpaLinkUpdater.update().stream().map(Link::url).toList();
        List<String> actualLinkList2 = jpaLinkUpdater.update().stream().map(Link::url).toList();
        //Then
        Assertions.assertAll(
            () -> assertThat(update).isNotZero(),
            () -> assertThat(actualLinkList1).containsOnlyOnceElementsOf(expectedLinkList1),
            () -> assertThat(actualLinkList2).containsOnlyOnceElementsOf(expectedLinkList2)
        );
    }

}
