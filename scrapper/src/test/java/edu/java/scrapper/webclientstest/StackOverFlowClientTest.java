package edu.java.scrapper.webclientstest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import edu.java.scrapper.webclients.clients.StackOverFlowClient;
import edu.java.scrapper.webclients.dto.stackoverflow.AnswerInfo;
import edu.java.scrapper.webclients.dto.stackoverflow.QuestionInfo;
import edu.java.scrapper.webclients.dto.stackoverflow.StackOverFlowQuestionResponse;
import edu.java.scrapper.webclients.dto.stackoverflow.StackOverFlowUser;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static java.time.Instant.ofEpochSecond;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StackOverFlowClientTest {

    private final static String TEST_BASE_URL = "http://localhost:8080/";
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
    private final static boolean TEST_IS_ACCEPTED = true;
    private final static int TEST_SCORE = 2500;

    @MockBean
    private ChatService chatService;
    @MockBean
    private LinkService linkService;
    @MockBean
    private LinkUpdater linkUpdater;

    @Autowired
    private StackOverFlowClient stackOverFlowClient;
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
    static void stubStackOverflowClientBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("app.stack-over-flow-settings.default-base-url", () -> "http://localhost:8080");
    }

    @ParameterizedTest
    @MethodSource("provideBaseUrls")
    @DisplayName("Test question info response.")
    void testQuestionInfoResponse(String baseUrl) {
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
        //Setting up test conditions
        stubFor(get("/questions/12345?site=stackoverflow").willReturn(okJson(getQuestionInfoJSONBody())));

        //When
        StackOverFlowQuestionResponse<QuestionInfo> actualResponse =
            stackOverFlowClient.getQuestionInfoResponse(12345L);

        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @ParameterizedTest
    @MethodSource("provideBaseUrls")
    @DisplayName("Test answer info response.")
    void testAnswerInfoResponse(String baseUrl) {
        //Given
        StackOverFlowQuestionResponse<AnswerInfo> expectedResponse =
            new StackOverFlowQuestionResponse<>(
                List.of(
                    new AnswerInfo(
                        new StackOverFlowUser(
                            TEST_USERNAME,
                            TEST_USER_REP
                        ),
                        TEST_IS_ACCEPTED,
                        TEST_SCORE,
                        ofEpochSecond(CREATION_EPOCH_SEC).atOffset(ZoneOffset.UTC),
                        ofEpochSecond(EDITED_EPOCH_SEC).atOffset(ZoneOffset.UTC),
                        ofEpochSecond(LAST_ACTIVITY_EPOCH_SEC).atOffset(ZoneOffset.UTC)
                    )
                ),
                TEST_HAS_MORE,
                TEST_MAX_QUOTA,
                TEST_QUOTA_REMAINING
            );
        //Setting up test conditions
        stubFor(get("/questions/12345/answers?site=stackoverflow").willReturn(okJson(getAnswersInfoJSONBody())));

        //When
        StackOverFlowQuestionResponse<AnswerInfo> actualResponse =
            stackOverFlowClient.getAnswerInfoResponse(12345L);

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

    private static String getAnswersInfoJSONBody() {
        return """
            {
                "items": [
                    {
                        "owner": {
                            "reputation": %d,
                            "display_name": "%s"
                        },
                        "is_accepted": %b,
                        "score": %d,
                        "last_activity_date": %d,
                        "last_edit_date": %d,
                        "creation_date": %d
                    }
                ],
                "has_more": %b,
                "quota_max": %d,
                "quota_remaining": %d
            }"""
            .formatted(
                TEST_USER_REP, TEST_USERNAME,
                TEST_IS_ACCEPTED,
                TEST_SCORE,
                LAST_ACTIVITY_EPOCH_SEC,
                EDITED_EPOCH_SEC,
                CREATION_EPOCH_SEC,
                TEST_HAS_MORE,
                TEST_MAX_QUOTA,
                TEST_QUOTA_REMAINING
            );
    }

}
