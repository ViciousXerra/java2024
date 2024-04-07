package edu.java.bot.scrapperclientstests.retrypoliticstests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.WithoutKafkaTestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@Import(WithoutKafkaTestConfig.class)
public abstract class RetryPoliticsTest {

    protected static final int ATTEMPTS_LIMIT = 3;
    protected static final int SUCCESS_RETRY_ATTEMPT_COUNT = 2;
    protected static final int REATTACH_DELTA = ATTEMPTS_LIMIT - SUCCESS_RETRY_ATTEMPT_COUNT;
    protected static final int INTERNAL_SERVER_ERROR_CODE = 500;
    protected static final int SUCCESS_CODE = 200;
    protected static WireMockServer mockServer;

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
    static void stubClientSettings(DynamicPropertyRegistry registry) {
        registry.add("app.scrapper-settings.default-base-url", () -> "http://localhost:8080");
        registry.add("app.client-retry-settings.attempts-limit", () -> "3");
        registry.add("app.client-retry-settings.attempt-delay", () -> "1s");
    }

}
