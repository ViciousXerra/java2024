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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BotUpdateClientTest {

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
    @Order(1)
    @DisplayName("Test POST exchange")
    void testPostExchange() {
        //Set up
        mockServer
            .stubFor(post(urlEqualTo("/updates"))
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
    @Order(2)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Test 4xx http status handler")
    void testClientErrorHandler() {
        //Set up
        mockServer
            .stubFor(post(urlEqualTo("/updates"))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(400)
                    .withBody("""
                        {
                            "description": "desc",
                            "code": "400",
                            "exceptionName":"exception_name",
                            "exceptionMessage": "exception_message",
                            "stacktrace":[
                                "frame",
                                "another_frame"
                            ]
                        }""")
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
