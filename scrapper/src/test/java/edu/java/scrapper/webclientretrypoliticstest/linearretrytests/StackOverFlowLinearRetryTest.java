package edu.java.scrapper.webclientretrypoliticstest.linearretrytests;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import edu.java.scrapper.webclients.clients.StackOverFlowClient;
import edu.java.scrapper.webclients.dto.stackoverflow.QuestionInfo;
import edu.java.scrapper.webclients.dto.stackoverflow.StackOverFlowQuestionResponse;
import edu.java.scrapper.webclients.dto.stackoverflow.StackOverFlowUser;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static java.time.Instant.ofEpochSecond;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StackOverFlowLinearRetryTest extends ClientLinearRetryTest {

    private static final String URL_PATH =
        "/questions/12345?site=stackoverflow";
    private static final long QUESTION_ID = 12345L;
    private final static String TEST_TAG1 = "test_tag1";
    private final static String TEST_TAG2 = "test_tag2";
    private final static String TEST_USERNAME = "test_user1";
    private final static int TEST_USER_REP = 100;
    private final static boolean TEST_IS_ANSWERED = false;
    private final static int TEST_ANSWER_COUNT = 5;
    private final static long LAST_ACTIVITY_EPOCH_SEC = 1000000L;
    private final static long CREATION_EPOCH_SEC = 50000L;
    private final static long EDITED_EPOCH_SEC = 75000L;
    private final static String TEST_TITLE = "test_title";
    private final static boolean TEST_HAS_MORE = false;
    private final static int TEST_MAX_QUOTA = 300;
    private final static int TEST_QUOTA_REMAINING = 275;
    private static final String GET_EXCHANGE_RETRY_EXHAUSTED = "test GET exchange retry exhausted";
    private static final String GET_EXCHANGE_RETRY_SUCCESS = "test GET exchange retry success";

    @Autowired
    private StackOverFlowClient stackOverFlowClient;

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
        assertThatThrownBy(() -> stackOverFlowClient.getQuestionInfoResponse(QUESTION_ID)).isInstanceOf(
            WebClientResponseException.class);
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
                .willReturn(okJson(getQuestionInfoJSONBody()))
            );
        //Given
        StackOverFlowQuestionResponse<QuestionInfo> expectedResponse =
            new StackOverFlowQuestionResponse<>(
                List.of(
                    new QuestionInfo(
                        TEST_TITLE, List.of(TEST_TAG1, TEST_TAG2),
                        new StackOverFlowUser(TEST_USERNAME, TEST_USER_REP),
                        TEST_IS_ANSWERED,
                        TEST_ANSWER_COUNT,
                        ofEpochSecond(CREATION_EPOCH_SEC).atOffset(ZoneOffset.UTC),
                        ofEpochSecond(EDITED_EPOCH_SEC).atOffset(ZoneOffset.UTC),
                        ofEpochSecond(LAST_ACTIVITY_EPOCH_SEC).atOffset(ZoneOffset.UTC)
                    )
                ),
                TEST_HAS_MORE,
                TEST_MAX_QUOTA,
                TEST_QUOTA_REMAINING
            );
        //When
        StackOverFlowQuestionResponse<QuestionInfo> actualResponse =
            stackOverFlowClient.getQuestionInfoResponse(QUESTION_ID);
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    private static String getQuestionInfoJSONBody() {
        return """
            {
                "items": [
                    {
                        "tags": [
                            "%s",
                            "%s"
                        ],
                        "owner": {
                            "reputation": %d,
                            "display_name": "%s"
                        },
                        "is_answered": %b,
                        "answer_count": %d,
                        "last_activity_date": %d,
                        "creation_date": %d,
                        "last_edit_date": %d,
                        "title": "%s"
                    }
                ],
                "has_more": %b,
                "quota_max": %d,
                "quota_remaining": %d
            }"""
            .formatted(
                TEST_TAG1, TEST_TAG2,
                TEST_USER_REP, TEST_USERNAME,
                TEST_IS_ANSWERED,
                TEST_ANSWER_COUNT,
                LAST_ACTIVITY_EPOCH_SEC,
                CREATION_EPOCH_SEC,
                EDITED_EPOCH_SEC,
                TEST_TITLE,
                TEST_HAS_MORE,
                TEST_MAX_QUOTA,
                TEST_QUOTA_REMAINING
            );
    }

}
