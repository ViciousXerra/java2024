package edu.java.bot;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.bot.scrapperclient.clients.LinksClient;
import edu.java.bot.scrapperclient.dto.requests.AddLinkRequest;
import edu.java.bot.scrapperclient.dto.requests.RemoveLinkRequest;
import edu.java.bot.scrapperclient.dto.responses.LinkResponse;
import edu.java.bot.scrapperclient.dto.responses.ListLinkResponse;
import java.net.URI;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LinksClientTest {

    private static WireMockServer mockServer;
    @Autowired
    private LinksClient linksClient;

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
        registry.add("app.scrapper-settings.default-base-url", () -> "http://localhost:8080");
    }

    @Test
    @Order(1)
    @DisplayName("Test DELETE exchange")
    void testDeleteExchange() {
        //Set up
        mockServer
            .stubFor(delete(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", equalTo("1"))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody("""
                        {
                            "id":1,
                            "url":"https://github.com"
                        }""")));
        //Given
        LinkResponse expectedResponse = new LinkResponse(1L, URI.create("https://github.com"));
        //When
        LinkResponse actualResponse = linksClient.removeLink(1L, new RemoveLinkRequest("https://github.com"));
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @Order(2)
    @DisplayName("Test POST exchange")
    void testPostExchange() {
        //Set up
        mockServer
            .stubFor(post(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", equalTo("1"))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody("""
                        {
                            "id":1,
                            "url":"https://github.com"
                        }""")));
        //Given
        LinkResponse expectedResponse = new LinkResponse(1L, URI.create("https://github.com"));
        //When
        LinkResponse actualResponse = linksClient.addLink(1L, new AddLinkRequest("https://github.com"));
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @Order(3)
    @DisplayName("Test GET exchange")
    void testGetExchange() {
        //Set up
        mockServer
            .stubFor(get(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", equalTo("1"))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody("""
                        {
                          "links": [
                            {
                              "id": 1,
                              "url": "https://github.com"
                            },
                            {
                              "id": 2,
                              "url": "https://stackoverflow.com"
                            }
                          ],
                          "size": 2
                        }""")));
        //Given
        ListLinkResponse expectedResponse = new ListLinkResponse(
            List.of(
                new LinkResponse(1L, URI.create("https://github.com")),
                new LinkResponse(2L, URI.create("https://stackoverflow.com"))
            ),
            2
        );
        //When
        ListLinkResponse actualResponse = linksClient.getAllLinks(1L);
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @Order(4)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Test 4xx http status handler")
    void testClientErrorHandler() {
        //Set up
        mockServer
            .stubFor(post(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", equalTo("1"))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(400)
                    .withBody("""
                        {
                           "description": "desc",
                           "code": "code",
                           "exceptionName": "exception",
                           "exceptionMessage": "message",
                           "stacktrace": [
                             "stackelement"
                           ]
                         }""")));
        //Then
        assertThatThrownBy(
            () -> linksClient.addLink(1L, new AddLinkRequest("https://github.com"))
        )
            .isInstanceOf(WebClientResponseException.class);
    }

}
