package edu.java.bot.scrapperclientstests.retrypoliticstests.fixedretrytests;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LinksClientFixedRetryTest extends ClientFixedRetryTest {

    private static final String ADD_DELETE_RESPONSE_BODY =
        """
            {
                "id":1,
                "url":"https://github.com"
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
    private static final String DELETE_EXCHANGE_RETRY_EXHAUSTED = "test DELETE exchange retry exhausted";
    private static final String DELETE_EXCHANGE_RETRY_SUCCESS = "test DELETE exchange retry success";
    private static final String POST_EXCHANGE_RETRY_EXHAUSTED = "test POST exchange retry exhausted";
    private static final String POST_EXCHANGE_RETRY_SUCCESS = "test POST exchange retry success";
    private static final String GET_EXCHANGE_RETRY_EXHAUSTED = "test GET exchange retry exhausted";
    private static final String GET_EXCHANGE_RETRY_SUCCESS = "test GET exchange retry success";
    @Autowired
    private LinksClient linksClient;

    @Test
    @DisplayName(DELETE_EXCHANGE_RETRY_EXHAUSTED)
    void testDeleteExchangeRetryExhausted() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(delete(urlEqualTo(URL_PATH))
                    .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                    .inScenario(DELETE_EXCHANGE_RETRY_EXHAUSTED)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(delete(urlEqualTo(URL_PATH))
                .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                .inScenario(DELETE_EXCHANGE_RETRY_EXHAUSTED)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
            );
        //Then
        assertThatThrownBy(() -> linksClient.removeLink(1L, new RemoveLinkRequest(LINK1))).isInstanceOf(
            WebClientResponseException.class);
    }

    @Test
    @DisplayName(DELETE_EXCHANGE_RETRY_SUCCESS)
    void testSuccessDeleteExchangeAfterRetry() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(delete(urlEqualTo(URL_PATH))
                    .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                    .inScenario(DELETE_EXCHANGE_RETRY_SUCCESS)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(delete(urlEqualTo(URL_PATH))
                .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                .inScenario(DELETE_EXCHANGE_RETRY_SUCCESS)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(aResponse()
                    .withHeader(CONTENT_HEADER_LABEL, CONTENT_HEADER_VALUE)
                    .withStatus(SUCCESS_CODE)
                    .withBody(ADD_DELETE_RESPONSE_BODY))
            );
        //Given
        LinkResponse expectedResponse = new LinkResponse(1L, URI.create(LINK1));
        //When
        LinkResponse actualResponse = linksClient.removeLink(1L, new RemoveLinkRequest(LINK1));
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName(POST_EXCHANGE_RETRY_EXHAUSTED)
    void testPostExchangeRetryExhausted() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(post(urlEqualTo(URL_PATH))
                    .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                    .inScenario(POST_EXCHANGE_RETRY_EXHAUSTED)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(post(urlEqualTo(URL_PATH))
                .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                .inScenario(POST_EXCHANGE_RETRY_EXHAUSTED)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
            );
        //Then
        assertThatThrownBy(() -> linksClient.addLink(1L, new AddLinkRequest(LINK1))).isInstanceOf(
            WebClientResponseException.class);
    }

    @Test
    @DisplayName(POST_EXCHANGE_RETRY_SUCCESS)
    void testSuccessPostExchangeAfterRetry() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(post(urlEqualTo(URL_PATH))
                    .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                    .inScenario(POST_EXCHANGE_RETRY_SUCCESS)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(post(urlEqualTo(URL_PATH))
                .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                .inScenario(POST_EXCHANGE_RETRY_SUCCESS)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(aResponse()
                    .withHeader(CONTENT_HEADER_LABEL, CONTENT_HEADER_VALUE)
                    .withStatus(SUCCESS_CODE)
                    .withBody(ADD_DELETE_RESPONSE_BODY))
            );
        //Given
        LinkResponse expectedResponse = new LinkResponse(1L, URI.create(LINK1));
        //When
        LinkResponse actualResponse = linksClient.addLink(1L, new AddLinkRequest(LINK1));
        //Then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName(GET_EXCHANGE_RETRY_EXHAUSTED)
    void testGetExchangeRetryExhausted() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(get(urlEqualTo(URL_PATH))
                    .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                    .inScenario(GET_EXCHANGE_RETRY_EXHAUSTED)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(get(urlEqualTo(URL_PATH))
                .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                .inScenario(GET_EXCHANGE_RETRY_EXHAUSTED)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
            );
        //Then
        assertThatThrownBy(() -> linksClient.getAllLinks(1L)).isInstanceOf(
            WebClientResponseException.class);
    }

    @Test
    @DisplayName(GET_EXCHANGE_RETRY_SUCCESS)
    void testSuccessGetExchangeAfterRetry() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(get(urlEqualTo(URL_PATH))
                    .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                    .inScenario(GET_EXCHANGE_RETRY_SUCCESS)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(get(urlEqualTo(URL_PATH))
                .withHeader(HEADER_LABEL, equalTo(HEADER_VALUE))
                .inScenario(GET_EXCHANGE_RETRY_SUCCESS)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(aResponse()
                    .withHeader(CONTENT_HEADER_LABEL, CONTENT_HEADER_VALUE)
                    .withStatus(SUCCESS_CODE)
                    .withBody(GET_RESPONSE_BODY))
            );
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

}
