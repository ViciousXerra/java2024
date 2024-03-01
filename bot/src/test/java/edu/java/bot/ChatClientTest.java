package edu.java.bot;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperclient.clients.ChatClient;
import edu.java.bot.scrapperclient.dto.errorresponses.ScrapperApiErrorResponse;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChatClientTest {

    private static WireMockServer mockServer;
    @Autowired
    private ChatClient chatClient;

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
    static void lstubScrapperBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("app.scrapper-settings.default-base-url", () -> "http://localhost:8080");
    }

    @Test
    @Order(1)
    @DisplayName("Test DELETE exchange")
    void testDeleteExchange() {
        //Set up
        mockServer
            .stubFor(delete(urlEqualTo("/tg-chat/1"))
                .willReturn(aResponse()
                    .withStatus(200)));
        //When
        ResponseEntity<?> actualResponse = chatClient.removeChat(1L);
        //Then
        assertThat(actualResponse.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("Test POST exchange")
    void testPostExchange() {
        //Set up
        mockServer
            .stubFor(post(urlEqualTo("/tg-chat/1"))
                .willReturn(aResponse()
                    .withStatus(200)));
        //When
        ResponseEntity<?> actualResponse = chatClient.signUpChat(1L);
        //Then
        assertThat(actualResponse.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    @Order(3)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Test 4xx http status handler")
    void testClientErrorHandler() {
        //Set up
        mockServer
            .stubFor(post(urlEqualTo("/tg-chat/1"))
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
        ScrapperApiErrorResponse expectedResponse = new ScrapperApiErrorResponse(
            "desc", "400", "exception_name", "exception_message",
            List.of("frame", "another_frame")
        );
        //Then
        assertThatThrownBy(
            () -> chatClient.signUpChat(1L)
        )
            .isInstanceOf(ClientException.class)
            .satisfies(exception -> assertThat(((ClientException) exception).getClientErrorResponseBody()).isEqualTo(
                expectedResponse));
    }

}
