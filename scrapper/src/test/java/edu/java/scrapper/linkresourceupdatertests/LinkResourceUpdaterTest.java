package edu.java.scrapper.linkresourceupdatertests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.schedulers.linkresourceupdaters.AbstractLinkResourceUpdater;
import edu.java.scrapper.schedulers.linkresourceupdaters.LinkUpdaterUtils;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LinkResourceUpdaterTest {

    private final static String GITHUB_API_STUB = "/repos/ViciousXerra/java2023/activity";
    private final static String STACKOVERFLOW_API_STUB = "/questions/11227809?site=stackoverflow";
    private final static String GITHUB_TEMPLATE = "https://github.com/ViciousXerra/java2023/tree/main/.mvn";
    private final static String STACKOVERFLOW_TEMPLATE =
        "https://stackoverflow.com/questions/11227809/why-is-processing-a-sorted-array-faster-than-processing-an-unsorted-array";
    private final static String GITHUB_ACTIVITY_LIST_JSON_TEMPLATE =
        """
            [
                {
                    "id": 1,
                    "ref": "refs/heads/branch1",
                    "timestamp": "%s",
                    "activity_type": "push",
                    "actor": {
                        "login": "user1",
                        "id": 4
                    }
                },
                {
                    "id": 2,
                    "ref": "refs/heads/branch2",
                    "timestamp": "%s",
                    "activity_type": "branch_creation",
                    "actor": {
                        "login": "user2",
                        "id": 5
                    }
                },
                {
                    "id": 3,
                    "ref": "refs/heads/branch3",
                    "timestamp": "%s",
                    "activity_type": "pr_merge",
                    "actor": {
                        "login": "user3",
                        "id": 6
                    }
                }
            ]
            """;
    private final static String STACKOVERFLOW_QUESTION_INFO_JSON_TEMPLATE =
        """
            {
                "items": [
                    {
                        "tags": [
                            "tag1",
                            "tag2"
                        ],
                        "owner": {
                                "reputation": 100,
                                "display_name": "name"
                        },
                        "is_answered": true,
                        "answer_count": 3,
                        "last_activity_date": %d,
                        "creation_date": 1710778394,
                        "last_edit_date": 1710778801,
                        "title": "title1"
                    }
                ],
                "has_more": false,
                "quota_max": 300,
                "quota_remaining": 296
            }
            """;

    private static final ZonedDateTime TEST_ZONED_DATE_TIME =
        ZonedDateTime.parse("2007-12-03T10:15:30", DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC));

    private static WireMockServer mockServer;

    @Autowired
    private AbstractLinkResourceUpdater resourceUpdater;

    @BeforeAll
    public static void setUpMockServer() {
        mockServer = new WireMockServer();
        mockServer.start();
        WireMock.configureFor("localhost", 8080);
    }

    @AfterEach
    public void resetMockServerState() {
        mockServer.resetAll();
    }

    @AfterAll
    public static void stopMockServer() {
        mockServer.stop();
        mockServer.shutdown();
    }

    @DynamicPropertySource
    static void stubApiBaseUrls(DynamicPropertyRegistry registry) {
        registry.add("app.stack-over-flow-settings.default-base-url", () -> "http://localhost:8080");
        registry.add("app.git-hub-settings.default-base-url", () -> "http://localhost:8080");
    }

    @Test
    @DisplayName("Test not URI pattern")
    void testNotUriPattern() {
        assertThatThrownBy(() -> resourceUpdater.process(new Link(
            1L,
            "not a URI pattern",
            ZonedDateTime.now(),
            ZonedDateTime.now()
        ), new HashMap<>()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unable to recognize URL pattern: not a URI pattern");
    }

    @Test
    @DisplayName("Test unsupported domain name")
    void testUnsupportedDomainName() {
        assertThatThrownBy(() -> resourceUpdater.process(new Link(
            1L,
            "https://www.example.co.uk:443/blog/article/search?docid=720&hl=en",
            ZonedDateTime.now(),
            ZonedDateTime.now()
        ), new HashMap<>()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unsupported domain: www.example.co.uk");
    }

    @Test
    @DisplayName("Test no github repo updates")
    void testNoGitHubRepoUpdates() {
        //set up
        stubFor(get(urlEqualTo(GITHUB_API_STUB)).willReturn(jsonResponse(getGitHubRepoActivityJsonBody(
            TEST_ZONED_DATE_TIME.minusDays(1L), TEST_ZONED_DATE_TIME.minusDays(2L), TEST_ZONED_DATE_TIME.minusDays(3L)
        ), 200)));
        //When
        Map<Link, ZonedDateTime> linkZonedDateTimeMap = new HashMap<>();
        Link link = new Link(1L, GITHUB_TEMPLATE, TEST_ZONED_DATE_TIME, TEST_ZONED_DATE_TIME);
        Map.Entry<LinkUpdaterUtils.Activity, String> actualResult =
            resourceUpdater.process(link, linkZonedDateTimeMap);
        //Then
        Assertions.assertAll(
            () -> assertThat(linkZonedDateTimeMap).isEmpty(),
            () -> assertThat(actualResult.getKey()).isEqualTo(LinkUpdaterUtils.Activity.NO_ACTIVITY),
            () -> assertThat(actualResult.getValue()).isNull()
        );
    }

    @ParameterizedTest
    @MethodSource("provideGitHubTest")
    @DisplayName("Test GitHub repo updates")
    void testGitHubResourceUpdater(
        String jsonResponseBody,
        Map.Entry<LinkUpdaterUtils.Activity, String> expectedResult
    ) {
        //set up
        stubFor(get(urlEqualTo(GITHUB_API_STUB)).willReturn(jsonResponse(jsonResponseBody, 200)));
        //When
        Map<Link, ZonedDateTime> linkZonedDateTimeMap = new HashMap<>();
        Link link = new Link(1L, GITHUB_TEMPLATE, TEST_ZONED_DATE_TIME, TEST_ZONED_DATE_TIME);
        Map.Entry<LinkUpdaterUtils.Activity, String> actualResult =
            resourceUpdater.process(link, linkZonedDateTimeMap);
        //Then
        Assertions.assertAll(
            () -> assertThat(linkZonedDateTimeMap.containsKey(link)),
            () -> assertThat(actualResult.getKey()).isEqualTo(expectedResult.getKey()),
            () -> assertThat(StringContains.containsString(actualResult.getValue()).matches(expectedResult))
        );
    }

    @Test
    @DisplayName("Test stackoverflow question no updates")
    void testStackOverFlowNoUpdates() {
        //set up
        stubFor(get(urlEqualTo(STACKOVERFLOW_API_STUB)).willReturn(jsonResponse(getStackOverFlowQuestionInfo(
            TEST_ZONED_DATE_TIME), 200)));
        Map<Link, ZonedDateTime> linkZonedDateTimeMap = new HashMap<>();
        Link link = new Link(1L, STACKOVERFLOW_TEMPLATE, TEST_ZONED_DATE_TIME, TEST_ZONED_DATE_TIME);
        Map.Entry<LinkUpdaterUtils.Activity, String> actualResult =
            resourceUpdater.process(link, linkZonedDateTimeMap);
        //Then
        Assertions.assertAll(
            () -> assertThat(linkZonedDateTimeMap).isEmpty(),
            () -> assertThat(actualResult.getKey()).isEqualTo(LinkUpdaterUtils.Activity.NO_ACTIVITY),
            () -> assertThat(actualResult.getValue()).isNull()
        );
    }

    @Test
    @DisplayName("Test stackoverflow question update")
    void testStackOverFlowUpdate() {
        //set up
        stubFor(get(urlEqualTo(STACKOVERFLOW_API_STUB)).willReturn(jsonResponse(getStackOverFlowQuestionInfo(
            TEST_ZONED_DATE_TIME.plusDays(1L)), 200)));
        Map<Link, ZonedDateTime> linkZonedDateTimeMap = new HashMap<>();
        Link link = new Link(1L, STACKOVERFLOW_TEMPLATE, TEST_ZONED_DATE_TIME, TEST_ZONED_DATE_TIME);
        Map.Entry<LinkUpdaterUtils.Activity, String> actualResult =
            resourceUpdater.process(link, linkZonedDateTimeMap);
        //Then
        Assertions.assertAll(
            () -> assertThat(linkZonedDateTimeMap.containsKey(link)),
            () -> assertThat(actualResult.getKey()).isEqualTo(LinkUpdaterUtils.Activity.NEW_UPDATE),
            () -> assertThat(actualResult.getValue()).isEqualTo("There is new activity in \"title1\" question thread")
        );
    }

    private static Object[][] provideGitHubTest() {
        return new Object[][] {
            {
                getGitHubRepoActivityJsonBody(
                    TEST_ZONED_DATE_TIME.plusDays(3L),
                    TEST_ZONED_DATE_TIME.plusDays(2L),
                    TEST_ZONED_DATE_TIME.plusDays(1L)
                ),
                new AbstractMap.SimpleEntry<>(
                    LinkUpdaterUtils.Activity.NEW_UPDATE,
                    """
                        https://github.com/ViciousXerra/java2023/tree/main/.mvn
                        User: user3
                        Date/Time: 2007-12-04T10:15:30Z
                        Activity: new update
                        Available on: refs/heads/branch3

                        User: user2
                        Date/Time: 2007-12-05T10:15:30Z
                        Activity: created new branch
                        Available on: refs/heads/branch2

                        User: user1
                        Date/Time: 2007-12-06T10:15:30Z
                        Activity: pushed new commits
                        Available on: refs/heads/branch1

                        """
                )
            },
            {
                getGitHubRepoActivityJsonBody(
                    TEST_ZONED_DATE_TIME.plusDays(3L),
                    TEST_ZONED_DATE_TIME.plusDays(2L),
                    TEST_ZONED_DATE_TIME
                ),
                new AbstractMap.SimpleEntry<>(
                    LinkUpdaterUtils.Activity.NEW_UPDATE,
                    """
                        https://github.com/ViciousXerra/java2023/tree/main/.mvn
                        User: user2
                        Date/Time: 2007-12-05T10:15:30Z
                        Activity: created new branch
                        Available on: refs/heads/branch2

                        User: user1
                        Date/Time: 2007-12-06T10:15:30Z
                        Activity: pushed new commits
                        Available on: refs/heads/branch1

                        """
                )
            },
            {
                getGitHubRepoActivityJsonBody(
                    TEST_ZONED_DATE_TIME.plusDays(3L),
                    TEST_ZONED_DATE_TIME,
                    TEST_ZONED_DATE_TIME
                ),
                new AbstractMap.SimpleEntry<>(
                    LinkUpdaterUtils.Activity.NEW_UPDATE,
                    """
                        https://github.com/ViciousXerra/java2023/tree/main/.mvn
                        User: user1
                        Date/Time: 2007-12-06T10:15:30Z
                        Activity: pushed new commits
                        Available on: refs/heads/branch1

                        """
                )
            }
        };
    }

    private static String getGitHubRepoActivityJsonBody(
        ZonedDateTime activityTime1,
        ZonedDateTime activityTime2,
        ZonedDateTime activityTime3
    ) {
        return GITHUB_ACTIVITY_LIST_JSON_TEMPLATE.formatted(
            activityTime1.format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)),
            activityTime2.format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)),
            activityTime3.format(DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC))
        );
    }

    private static String getStackOverFlowQuestionInfo(
        ZonedDateTime questionLastActivity
    ) {
        return STACKOVERFLOW_QUESTION_INFO_JSON_TEMPLATE.formatted(questionLastActivity.toEpochSecond());
    }

}
