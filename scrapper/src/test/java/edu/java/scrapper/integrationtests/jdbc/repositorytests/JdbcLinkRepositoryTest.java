package edu.java.scrapper.integrationtests.jdbc.repositorytests;

import edu.java.scrapper.dao.repository.dto.Link;
import edu.java.scrapper.dao.repository.dto.mappers.LinkRowMapper;
import edu.java.scrapper.dao.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.integrationtests.IntegrationTest;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

public class JdbcLinkRepositoryTest extends IntegrationTest {

    private final static RowMapper<Link> ROW_MAPPER = new LinkRowMapper();
    private final static Function<List<Link>, List<String>> URL_CONVERT_LAMBDA =
        links -> links.stream().map(Link::url).toList();
    private final static Function<List<Link>, List<Long>> ID_CONVERT_LAMBDA =
        links -> links.stream().map(Link::linkId).toList();
    private final static Function<List<Link>, List<ZonedDateTime>> ZONED_DT_CHECKED_AT_CONVERT_LAMBDA =
        links -> links.stream().map(Link::checkedAt).toList();

    @Autowired
    private JdbcLinkRepository linkRepository;
    @Autowired
    private JdbcClient jdbcClient;

    @Test
    @DisplayName("Test \"add\" link repository method")
    @Transactional
    @Rollback
    void addLinkTest() {
        //Given
        List<String> expectedUrls = List.of("link1", "link2", "link3");
        //When
        linkRepository.add("link1");
        linkRepository.add("link2");
        linkRepository.add("link3");
        List<Link> actualLinks =
            jdbcClient.sql("SELECT * FROM Link").query(ROW_MAPPER).list();
        //Then
        Assertions.assertAll(
            () -> assertThat(URL_CONVERT_LAMBDA.apply(actualLinks)).containsOnlyOnceElementsOf(expectedUrls),
            () -> assertThat(ID_CONVERT_LAMBDA.apply(actualLinks)).doesNotHaveDuplicates()
        );
    }

    @Test
    @DisplayName("Test \"remove\" link repository method")
    @Transactional
    @Rollback
    void removeLinkTest() {
        //Given
        List<String> expectedUrls = List.of("link1", "link3");
        //When
        jdbcClient.sql("INSERT INTO Link (url) VALUES ('link1'), ('link2'), ('link3')").update();
        linkRepository.remove("link2");
        List<Link> actualLinks =
            jdbcClient.sql("SELECT * FROM Link").query(ROW_MAPPER).list();
        //Then
        Assertions.assertAll(
            () -> assertThat(URL_CONVERT_LAMBDA.apply(actualLinks)).containsOnlyOnceElementsOf(expectedUrls),
            () -> assertThat(ID_CONVERT_LAMBDA.apply(actualLinks)).doesNotHaveDuplicates()
        );
    }

    @Test
    @DisplayName("Test \"find all\" link repository method")
    @Transactional
    @Rollback
    void findAllLinkTest() {
        //Given
        List<String> expectedUrls = List.of("link1", "link2", "link3");
        //When
        jdbcClient.sql("INSERT INTO Link (url) VALUES ('link1'), ('link2'), ('link3')").update();
        List<Link> actualLinks = linkRepository.findAll();
        //Then
        Assertions.assertAll(
            () -> assertThat(URL_CONVERT_LAMBDA.apply(actualLinks)).containsOnlyOnceElementsOf(expectedUrls),
            () -> assertThat(ID_CONVERT_LAMBDA.apply(actualLinks)).doesNotHaveDuplicates()
        );
    }

    @Test
    @DisplayName("Test \"find up to check\" link repository method")
    @Transactional
    @Rollback
    void findUpToCheckLinkTest() {
        //When
        jdbcClient.sql("INSERT INTO Link (url, checked_at) " +
                       "VALUES " +
                       "('link1', '2004-10-21 10:23:54+03')," +
                       "('link2', '2004-10-16 10:23:54+02')," +
                       "('link3', '2004-10-19 10:23:54+01')").update();
        List<Link> actualLinks = linkRepository.findUpToCheck(2);
        //Then
        Assertions.assertAll(
            () -> assertThat(URL_CONVERT_LAMBDA.apply(actualLinks)).containsExactly("link2", "link3"),
            () -> assertThat(ZONED_DT_CHECKED_AT_CONVERT_LAMBDA.apply(actualLinks)).containsExactly(
                ZonedDateTime.parse("2004-10-16T08:23:54Z"),
                ZonedDateTime.parse("2004-10-19T09:23:54Z")
            )
        );
    }

    @Test
    @DisplayName("Test \"modify checked_at\" link repository method")
    @Transactional
    @Rollback
    void modifyCheckedAtTest() {
        //Given
        ZonedDateTime expectedCheckedAtDateTime = ZonedDateTime.parse("2004-10-23T08:23:54Z");
        ZonedDateTime expectedUpdatedAtDateTime = ZonedDateTime.parse("2004-10-16T07:23:54Z");
        //When
        jdbcClient.sql(
            "INSERT INTO Link (url, checked_at, updated_at) VALUES ('link1', '2004-10-16 11:23:54+03', '2004-10-16 08:23:54+01')"
        ).update();
        linkRepository.modifyCheckedAtTimestamp("link1", ZonedDateTime.parse("2004-10-23T08:23:54Z"));
        ZonedDateTime[] actualDateTimes =
            jdbcClient.sql("SELECT checked_at, updated_at FROM Link").query(
                (rs, rowCol) -> new ZonedDateTime[] {
                    rs.getTimestamp("checked_at").toInstant().atZone(ZoneOffset.UTC),
                    rs.getTimestamp("updated_at").toInstant().atZone(ZoneOffset.UTC)
                }
            ).single();
        //Then
        Assertions.assertAll(
            () -> assertThat(actualDateTimes[0]).isEqualTo(expectedCheckedAtDateTime),
            () -> assertThat(actualDateTimes[1]).isEqualTo(expectedUpdatedAtDateTime)
        );
    }

    @Test
    @DisplayName("Test \"modify updated_at\" link repository method")
    @Transactional
    @Rollback
    void modifyUpdatedAtTest() {
        //Given
        ZonedDateTime expectedCheckedAtDateTime = ZonedDateTime.parse("2004-10-23T08:23:54Z");
        ZonedDateTime expectedUpdatedAtDateTime = ZonedDateTime.parse("2004-10-23T07:23:54Z");
        //When
        jdbcClient.sql(
            "INSERT INTO Link (url, checked_at, updated_at) VALUES ('link1', '2004-10-16 11:23:54+03', '2004-10-16 08:23:54+01')"
        ).update();
        linkRepository.modifyUpdatedAtTimestamp(
            "link1",
            ZonedDateTime.parse("2004-10-23T08:23:54Z"),
            ZonedDateTime.parse("2004-10-23T07:23:54Z")
        );
        ZonedDateTime[] actualDateTimes =
            jdbcClient.sql("SELECT checked_at, updated_at FROM Link").query(
                (rs, rowCol) -> new ZonedDateTime[] {
                    rs.getTimestamp("checked_at").toInstant().atZone(ZoneOffset.UTC),
                    rs.getTimestamp("updated_at").toInstant().atZone(ZoneOffset.UTC)
                }
            ).single();
        //Then
        Assertions.assertAll(
            () -> assertThat(actualDateTimes[0]).isEqualTo(expectedCheckedAtDateTime),
            () -> assertThat(actualDateTimes[1]).isEqualTo(expectedUpdatedAtDateTime)
        );
    }

}
