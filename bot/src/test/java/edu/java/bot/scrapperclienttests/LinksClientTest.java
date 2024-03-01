package edu.java.bot.scrapperclienttests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.bot.scrapperclient.clients.LinksClient;
import edu.java.bot.scrapperclient.dto.requests.AddLinkRequest;
import edu.java.bot.scrapperclient.dto.requests.RemoveLinkRequest;
import edu.java.bot.scrapperclient.dto.responses.LinkResponse;
import java.net.URI;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
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
    }

    @DynamicPropertySource
    static void stubScrapperBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("app.scrapper-settings.default-base-url", () -> "http://localhost:8080");
    }

    @Test
    @DisplayName("Test DELETE exchange")
    void testDeleteExchange() {
        mockServer
            .stubFor(delete(urlEqualTo("/links"))
                .withHeader("Tg-Chat-Id", equalTo("3"))
                .willReturn(aResponse()
                    .withHeader("Content-Type", "application/json")
                    .withStatus(200)
                    .withBody("""
                        {
                            "id":3,
                            "url":"https://github.com"
                        }""")));
        LinkResponse expectedResponse = new LinkResponse(3L, URI.create("https://github.com"));
        LinkResponse actualResponse = linksClient.removeLink(3L, new RemoveLinkRequest("https://github.com"));
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Test POST exchange")
    void testPostExchange() {
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
        LinkResponse expectedResponse = new LinkResponse(1L, URI.create("https://github.com"));
        LinkResponse actualResponse = linksClient.addLink(1L, new AddLinkRequest("https://github.com"));
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

}
