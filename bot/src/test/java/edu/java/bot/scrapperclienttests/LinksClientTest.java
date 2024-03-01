package edu.java.bot.scrapperclienttests;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.scrapperclient.clients.LinksClient;
import edu.java.bot.scrapperclient.dto.requests.AddLinkRequest;
import edu.java.bot.scrapperclient.dto.requests.RemoveLinkRequest;
import edu.java.bot.scrapperclient.dto.responses.LinkResponse;
import edu.java.bot.scrapperclient.dto.responses.ListLinkResponse;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@WireMockTest(httpPort = 8080)
@SpringBootTest
class LinksClientTest {

    @Autowired
    private LinksClient linksClient;

    @DynamicPropertySource
    static void stubScrapperBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("app.scrapper-settings.default-base-url", () -> "http://localhost:8080");
    }

    @Test
    @DisplayName("Test GET exchange")
    void testGetExchange() {
        //set up
        stubFor(get("/links").withHeader("Tg-Chat-Id", containing("1")).willReturn(okJson("""
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
            }""").withStatus(200)));
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
    @DisplayName("Test POST exchange")
    void testPostExchange() {
        //set up
        stubFor(post("/links").withHeader("Tg-Chat-Id", containing("1")).willReturn(okJson("""
            {
              "id": 1,
              "url": "https://github.com"
            }""").withStatus(200)));
        //Given
        LinkResponse expectedResponse = new LinkResponse(
            1L,
            URI.create("https://github.com")
        );
        //When
        LinkResponse actualResponse = linksClient.addLink(1L, new AddLinkRequest("https://github.com"));
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void testDeleteExchange() {
        //set up
        stubFor(delete("/links").withHeader("Tg-Chat-Id", containing("1")).willReturn(okJson("""
            {
              "id": 1,
              "url": "https://github.com"
            }""").withStatus(200)));
        //Given
        LinkResponse expectedResponse = new LinkResponse(
            1L,
            URI.create("https://github.com")
        );
        //When
        LinkResponse actualResponse = linksClient.removeLink(1L, new RemoveLinkRequest("https://github.com"));
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

}
