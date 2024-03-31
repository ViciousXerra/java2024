package edu.java.bot.scrapperclientstests.retrypoliticstests.fixedretrytests;

import com.github.tomakehurst.wiremock.stubbing.Scenario;
import edu.java.bot.scrapperclient.clients.ChatClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChatClientFixedRetryTest extends ClientFixedRetryTest {

    private static final String URL_PATH = "/tg-chat/1";
    private static final String DELETE_EXCHANGE_RETRY_EXHAUSTED = "test DELETE exchange retry exhausted";
    private static final String DELETE_EXCHANGE_RETRY_SUCCESS = "test DELETE exchange retry success";
    private static final String POST_EXCHANGE_RETRY_EXHAUSTED = "test POST exchange retry exhausted";
    private static final String POST_EXCHANGE_RETRY_SUCCESS = "test POST exchange retry success";
    @Autowired
    private ChatClient chatClient;

    @Test
    @DisplayName(DELETE_EXCHANGE_RETRY_EXHAUSTED)
    void testDeleteExchangeRetryExhausted() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(delete(urlEqualTo(URL_PATH))
                    .inScenario(DELETE_EXCHANGE_RETRY_EXHAUSTED)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(delete(urlEqualTo(URL_PATH))
                .inScenario(DELETE_EXCHANGE_RETRY_EXHAUSTED)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
            );
        //Then
        assertThatThrownBy(() -> chatClient.removeChat(1L)).isInstanceOf(WebClientResponseException.class);
    }

    @Test
    @DisplayName(DELETE_EXCHANGE_RETRY_SUCCESS)
    void testSuccessDeleteExchangeAfterRetry() {
        //Set up
        for (int i = 0; i < ATTEMPTS_LIMIT - REATTACH_DELTA; i++) {
            mockServer
                .stubFor(delete(urlEqualTo(URL_PATH))
                    .inScenario(DELETE_EXCHANGE_RETRY_SUCCESS)
                    .whenScenarioStateIs(i == 0 ? Scenario.STARTED : String.valueOf(i))
                    .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR_CODE))
                    .willSetStateTo(String.valueOf(i + 1))
                );
        }
        mockServer
            .stubFor(delete(urlEqualTo(URL_PATH))
                .inScenario(DELETE_EXCHANGE_RETRY_SUCCESS)
                .whenScenarioStateIs(String.valueOf(SUCCESS_RETRY_ATTEMPT_COUNT))
                .willReturn(aResponse().withStatus(SUCCESS_CODE))
            );
        //Then
        ResponseEntity<?> response = chatClient.removeChat(1L);
        assertThat(response.getStatusCode().value()).isEqualTo(SUCCESS_CODE);
    }

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
        assertThatThrownBy(() -> chatClient.signUpChat(1L)).isInstanceOf(WebClientResponseException.class);
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
        //Then
        ResponseEntity<?> response = chatClient.signUpChat(1L);
        assertThat(response.getStatusCode().value()).isEqualTo(SUCCESS_CODE);
    }

}
