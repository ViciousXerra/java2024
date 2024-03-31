package edu.java.scrapper.webclientretrypoliticstest.linearretrytests;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import edu.java.scrapper.webclients.clients.GitHubClient;
import edu.java.scrapper.webclients.dto.github.GitHubUser;
import edu.java.scrapper.webclients.dto.github.RepositoryActivityResponse;
import edu.java.scrapper.webclients.dto.github.RepositoryActivityType;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GitHubClientLinearRetryTest extends ClientLinearRetryTest {

    private static final String URL_PATH = "/repos/test_user/test_repo/activity";
    private final static String TEST_REPOSITORY_FULL_NAME = "test_repo";
    private final static String TEST_USER_NAME = "test_user";
    private final static long TEST_USER_ID = 10L;
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
    private static final String GET_EXCHANGE_RETRY_EXHAUSTED = "test GET exchange retry exhausted";
    private static final String GET_EXCHANGE_RETRY_SUCCESS = "test GET exchange retry success";

    @Autowired
    private GitHubClient gitHubClient;

    @Test
    @DisplayName(GET_EXCHANGE_RETRY_EXHAUSTED)
    void testGetExchangeRetryExhausted() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(get(urlEqualTo(URL_PATH))
                    .inScenario(GET_EXCHANGE_RETRY_EXHAUSTED)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(get(urlEqualTo(URL_PATH))
                .inScenario(GET_EXCHANGE_RETRY_EXHAUSTED)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
            );
        //Then
        assertThatThrownBy(() -> gitHubClient.getActivityListResponse(
            TEST_USER_NAME,
            TEST_REPOSITORY_FULL_NAME
        )).isInstanceOf(WebClientResponseException.class);
    }

    @Test
    @DisplayName(GET_EXCHANGE_RETRY_SUCCESS)
    void testGetSuccessExchangeAfterRetry() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(get(urlEqualTo(URL_PATH))
                    .inScenario(GET_EXCHANGE_RETRY_SUCCESS)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(get(urlEqualTo(URL_PATH))
                .inScenario(GET_EXCHANGE_RETRY_SUCCESS)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(okJson(getRepositoryActivityJSONBody()))
            );
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
        //When
        List<RepositoryActivityResponse> actualResponse = gitHubClient.getActivityListResponse(
            TEST_USER_NAME,
            TEST_REPOSITORY_FULL_NAME
        );
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
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
