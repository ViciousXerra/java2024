package edu.java.bot.scrapperclienttests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.bot.scrapperclient.clients.ChatClient;
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
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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
    }

    @DynamicPropertySource
    static void stubScrapperBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("app.scrapper-settings.default-base-url", () -> "http://localhost:8080");
    }

    @Test
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

}
