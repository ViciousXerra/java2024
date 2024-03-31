package edu.java.scrapper.webclientstest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.scrapper.webclients.clients.GitHubClient;
import edu.java.scrapper.webclients.dto.github.GitHubUser;
import edu.java.scrapper.webclients.dto.github.RepositoryActivityResponse;
import edu.java.scrapper.webclients.dto.github.RepositoryActivityType;
import edu.java.scrapper.webclients.dto.github.RepositoryGeneralInfoResponse;
import edu.java.scrapper.webclients.dto.github.RepositoryVisibility;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GitHubClientTest {

    private final static String TEST_BASE_URL = "http://localhost:8080/";
    private final static String TEST_REPOSITORY_FULL_NAME = "test_repo";
    private final static long TEST_REPOSITORY_ID = 1L;
    private final static String TEST_USER_NAME = "test_user";
    private final static long TEST_USER_ID = 10L;
    private final static String TEST_CREATED_AT_DATE_TIME = "2024-01-01T01:02:03Z";
    private final static String TEST_UPDATED_AT_DATE_TIME = "2024-01-01T04:05:06Z";
    private final static String TEST_PUSHED_AT_DATE_TIME = "2024-01-01T08:09:10Z";
    private final static String TEST_PUBLIC_VISIBILITY = "public";
    private final static int TEST_SUBSCRIBERS_COUNT = 5;
    private final static long TEST_ACTIVITY_ID1 = 125L;
    private final static long TEST_ACTIVITY_ID2 = 250L;
    private final static String TEST_ACTIVITY_REF1 = "refs/heads/test-branch1";
    private final static String TEST_ACTIVITY_REF2 = "refs/heads/test-branch2";
    private final static String TEST_ACTIVITY_TIMESTAMP1 = "2024-02-02T11:34:56Z";
    private final static String TEST_ACTIVITY_TIMESTAMP2 = "2024-01-01T11:00:50Z";
    private final static String TEST_USER_NAME_ANOTHER = "another_test_user";
    private final static long TEST_USER_ID_ANOTHER = 25L;
    private final static String TEST_ACTIVITY_TYPE1 = "push";
    private final static String TEST_ACTIVITY_TYPE2 = "branch_creation";

    @Autowired
    private GitHubClient gitHubClient;
    private static WireMockServer mockServer;

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
    static void stubGitHubClientBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("app.git-hub-settings.default-base-url", () -> "http://localhost:8080");
    }

    @ParameterizedTest
    @MethodSource("provideBaseUrls")
    @DisplayName("Test repository general info response.")
    void testRepositoryGeneralInfo(String baseUrl) {
        //Given
        RepositoryGeneralInfoResponse expectedResponse = new RepositoryGeneralInfoResponse(
            TEST_REPOSITORY_ID,
            TEST_REPOSITORY_FULL_NAME,
            new GitHubUser(TEST_USER_NAME, TEST_USER_ID),
            OffsetDateTime.parse(TEST_CREATED_AT_DATE_TIME),
            OffsetDateTime.parse(TEST_UPDATED_AT_DATE_TIME),
            OffsetDateTime.parse(TEST_PUSHED_AT_DATE_TIME),
            TEST_SUBSCRIBERS_COUNT,
            RepositoryVisibility.PUBLIC
        );
        //Setting up test conditions
        stubFor(get("/repos/test_user/test_repo").willReturn(okJson(getRepositoryJSONBody())));

        //When
        RepositoryGeneralInfoResponse actualResponse = gitHubClient.getGeneralInfoResponse(
            TEST_USER_NAME,
            TEST_REPOSITORY_FULL_NAME
        );

        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @MethodSource("provideBaseUrls")
    @DisplayName("Test list of repository activity response.")
    void testRepositoryActivity(String baseUrl) {
        //Given
        List<RepositoryActivityResponse> expectedResponse =
            List.of(
                new RepositoryActivityResponse(
                    TEST_ACTIVITY_ID1,
                    new GitHubUser(TEST_USER_NAME, TEST_USER_ID),
                    TEST_ACTIVITY_REF1,
                    OffsetDateTime.parse(TEST_ACTIVITY_TIMESTAMP1),
                    RepositoryActivityType.PUSH
                ),
                new RepositoryActivityResponse(
                    TEST_ACTIVITY_ID2,
                    new GitHubUser(TEST_USER_NAME_ANOTHER, TEST_USER_ID_ANOTHER),
                    TEST_ACTIVITY_REF2,
                    OffsetDateTime.parse(TEST_ACTIVITY_TIMESTAMP2),
                    RepositoryActivityType.BRANCH_CREATION
                )
            );
        //Setting up test conditions
        stubFor(get("/repos/test_user/test_repo/activity").willReturn(okJson(getRepositoryActivityJSONBody())));

        //When
        List<RepositoryActivityResponse> actualResponse = gitHubClient.getActivityListResponse(
            TEST_USER_NAME,
            TEST_REPOSITORY_FULL_NAME
        );

        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    private static Object[][] provideBaseUrls() {
        return new Object[][] {
            {
                TEST_BASE_URL
            },
            {
                null
            },
            {
                ""
            },
            {
                "    "
            }
        };
    }

    private static String getRepositoryJSONBody() {
        return """
            {
                "id": %d,
                "full_name": "%s",
                "owner": {
                    "login": "%s",
                    "id": %d
                },
                "created_at": "%s",
                "updated_at": "%s",
                "pushed_at": "%s",
                "visibility": "%s",
                "subscribers_count": %d
            }"""
            .formatted(
                TEST_REPOSITORY_ID, TEST_REPOSITORY_FULL_NAME, TEST_USER_NAME, TEST_USER_ID,
                TEST_CREATED_AT_DATE_TIME,
                TEST_UPDATED_AT_DATE_TIME,
                TEST_PUSHED_AT_DATE_TIME,
                TEST_PUBLIC_VISIBILITY,
                TEST_SUBSCRIBERS_COUNT
            );
    }

    private static String getRepositoryActivityJSONBody() {
        return """
            [
                {
                    "id": %d,
                    "ref": "%s",
                    "timestamp": "%s",
                    "activity_type": "%s",
                    "actor": {
                        "login": "%s",
                        "id": %d
                    }
                },
                {
                    "id": %d,
                    "ref": "%s",
                    "timestamp": "%s",
                    "activity_type": "%s",
                    "actor": {
                        "login": "%s",
                        "id": %d
                    }
                }
            ]"""
            .formatted(
                TEST_ACTIVITY_ID1, TEST_ACTIVITY_REF1, TEST_ACTIVITY_TIMESTAMP1, TEST_ACTIVITY_TYPE1,
                TEST_USER_NAME, TEST_USER_ID,
                TEST_ACTIVITY_ID2, TEST_ACTIVITY_REF2, TEST_ACTIVITY_TIMESTAMP2, TEST_ACTIVITY_TYPE2,
                TEST_USER_NAME_ANOTHER, TEST_USER_ID_ANOTHER
            );
    }

}
