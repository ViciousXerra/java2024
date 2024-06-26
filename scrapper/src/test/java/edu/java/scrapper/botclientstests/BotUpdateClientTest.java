package edu.java.scrapper.botclientstests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.scrapper.telegrambotclient.ClientException;
import edu.java.scrapper.telegrambotclient.clients.BotUpdateClient;
import edu.java.scrapper.telegrambotclient.dto.errorresponses.BotApiErrorResponse;
import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class BotUpdateClientTest {

    private static final String CONVERTED_API_ERROR_RESPONSE_BODY =
        """
            {
                "description": "desc",
                "code": "400",
                "exceptionName":"exception_name",
                "exceptionMessage": "exception_message",
                "stacktrace":[
                    "frame",
                    "another_frame"
                ]
            }
            """;
    private static final String URL_PATH = "/updates";
    private static WireMockServer mockServer;
    @Autowired
    private BotUpdateClient botClient;

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
    static void stubScrapperBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("app.bot-settings.default-base-url", () -> "http://localhost:8080");
    }

    @Test
    @DisplayName("Test POST exchange")
    void testPostExchange() {
        //Set up
        mockServer
            .stubFor(post(urlEqualTo(URL_PATH))
                .willReturn(aResponse()
                    .withStatus(200)));
        //When
        ResponseEntity<?> actualResponse = botClient.postLinkUpdate(
            new LinkUpdate(1L, "https://github.com", "desc", List.of(1L, 2L, 3L))
        );
        //Then
        assertThat(actualResponse.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @DisplayName("Test 4xx http status handler")
    void testClientErrorHandler() {
        //Set up
        mockServer
            .stubFor(post(urlEqualTo(URL_PATH))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(400)
                    .withBody(CONVERTED_API_ERROR_RESPONSE_BODY)
                )
            );
        //Given
        BotApiErrorResponse expectedResponse = new BotApiErrorResponse(
            "desc", "400", "exception_name", "exception_message",
            List.of("frame", "another_frame")
        );
        //Then
        assertThatThrownBy(
            () -> botClient.postLinkUpdate(
                new LinkUpdate(1L, "https://stackoverflow.com", "desc", List.of(1L, 2L, 3L))
            )
        )
            .isInstanceOf(ClientException.class)
            .satisfies(exception -> assertThat(((ClientException) exception).getBotApiErrorResponse()).isEqualTo(
                expectedResponse));
    }

}
