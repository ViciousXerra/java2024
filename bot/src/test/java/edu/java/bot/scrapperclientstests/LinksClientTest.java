package edu.java.bot.scrapperclientstests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.WithoutKafkaTestConfig;
import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperclient.clients.LinksClient;
import edu.java.bot.scrapperclient.dto.errorresponses.ScrapperApiErrorResponse;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(WithoutKafkaTestConfig.class)
class LinksClientTest {

    private static final String ADD_DELETE_RESPONSE_BODY =
        """
        {
            "id":1,
            "url":"https://github.com"
        }
        """;
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
    private static final String GET_RESPONSE_BODY =
        """
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
        }
        """;
    private static final String HEADER_LABEL = "Tg-Chat-Id";
    private static final String HEADER_VALUE = "1";
    private static final String CONTENT_HEADER_LABEL = "Content-Type";
    private static final String CONTENT_HEADER_VALUE = "application/json";
    private static final String LINK1 = "https://github.com";
    private static final String LINK2 = "https://stackoverflow.com";
    private static final String URL_PATH = "/links";
    private static WireMockServer mockServer;
    @Autowired
    private LinksClient linksClient;
    @MockBean
    private TelegramBot bot;

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
    @DisplayName("Test DELETE exchange")
    void testDeleteExchange() {
        //Set up
        mockServer
            .stubFor(delete(urlEqualTo(URL_PATH))
                .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                .willReturn(aResponse()
                    .withHeader(CONTENT_HEADER_LABEL, CONTENT_HEADER_VALUE)
                    .withStatus(200)
                    .withBody(ADD_DELETE_RESPONSE_BODY)));
        //Given
        LinkResponse expectedResponse = new LinkResponse(1L, URI.create(LINK1));
        //When
        LinkResponse actualResponse = linksClient.removeLink(1L, new RemoveLinkRequest(LINK1));
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Test POST exchange")
    void testPostExchange() {
        //Set up
        mockServer
            .stubFor(post(urlEqualTo(URL_PATH))
                .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                .willReturn(aResponse()
                    .withHeader(CONTENT_HEADER_LABEL, CONTENT_HEADER_VALUE)
                    .withStatus(200)
                    .withBody(
                        ADD_DELETE_RESPONSE_BODY)));
        //Given
        LinkResponse expectedResponse = new LinkResponse(1L, URI.create(LINK1));
        //When
        LinkResponse actualResponse = linksClient.addLink(1L, new AddLinkRequest(LINK1));
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Test GET exchange")
    void testGetExchange() {
        //Set up
        mockServer
            .stubFor(get(urlEqualTo(URL_PATH))
                .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                .willReturn(aResponse()
                    .withHeader(CONTENT_HEADER_LABEL, CONTENT_HEADER_VALUE)
                    .withStatus(200)
                    .withBody(GET_RESPONSE_BODY)));
        //Given
        ListLinkResponse expectedResponse = new ListLinkResponse(
            List.of(
                new LinkResponse(1L, URI.create(LINK1)),
                new LinkResponse(2L, URI.create(LINK2))
            ),
            2
        );
        //When
        ListLinkResponse actualResponse = linksClient.getAllLinks(1L);
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Test 4xx http status handler")
    void testClientErrorHandler() {
        //Set up
        mockServer
            .stubFor(post(urlEqualTo(URL_PATH))
                .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                .willReturn(aResponse()
                    .withHeader(CONTENT_HEADER_LABEL, CONTENT_HEADER_VALUE)
                    .withStatus(400)
                    .withBody(CONVERTED_API_ERROR_RESPONSE_BODY)
                )
            );
        //Given
        ScrapperApiErrorResponse expectedResponse = new ScrapperApiErrorResponse(
            "desc", "400", "exception_name", "exception_message",
            List.of("frame", "another_frame")
        );
        //Then
        assertThatThrownBy(
            () -> linksClient.addLink(1L, new AddLinkRequest("https://stackoverflow.com"))
        )
            .isInstanceOf(ClientException.class)
            .satisfies(exception -> assertThat(((ClientException) exception).getClientErrorResponseBody()).isEqualTo(
                expectedResponse));
    }

}
