package edu.java.scrapper.webclientretrypoliticstest.fixedretrytests;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import edu.java.scrapper.telegrambotclient.clients.BotUpdateClient;
import edu.java.scrapper.telegrambotclient.dto.requests.LinkUpdate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BotUpdatesFixedRetryTest extends ClientFixedRetryTest {

    private static final String URL_PATH = "/updates";
    private static final String POST_EXCHANGE_RETRY_EXHAUSTED = "test POST exchange retry exhausted";
    private static final String POST_EXCHANGE_RETRY_SUCCESS = "test POST exchange retry success";
    private static final LinkUpdate LINK_UPDATE = new LinkUpdate(
        1L,
        "https://github.com",
        "desc",
        List.of(1L, 2L, 3L)
    );
    @Autowired
    private BotUpdateClient botClient;

    @Test
    @DisplayName(POST_EXCHANGE_RETRY_EXHAUSTED)
    void testPostExchangeRetryExhausted() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(post(urlEqualTo(URL_PATH))
                    .inScenario(POST_EXCHANGE_RETRY_EXHAUSTED)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(post(urlEqualTo(URL_PATH))
                .inScenario(POST_EXCHANGE_RETRY_EXHAUSTED)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
            );
        //Then
        assertThatThrownBy(() -> botClient.postLinkUpdate(LINK_UPDATE)).isInstanceOf(WebClientResponseException.class);
    }

    @Test
    @DisplayName(POST_EXCHANGE_RETRY_SUCCESS)
    void testSuccessPostExchangeAfterRetry() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(post(urlEqualTo(URL_PATH))
                    .inScenario(POST_EXCHANGE_RETRY_SUCCESS)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(post(urlEqualTo(URL_PATH))
                .inScenario(POST_EXCHANGE_RETRY_SUCCESS)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(aResponse().withStatus(SUCCESS_CODE))
            );
        //When
        ResponseEntity<?> response = botClient.postLinkUpdate(LINK_UPDATE);
        //Then
        assertThat(response.getStatusCode().value()).isEqualTo(SUCCESS_CODE);
    }

}
