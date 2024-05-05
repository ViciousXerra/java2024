package edu.java.scrapper.webclientretrypoliticstest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import edu.java.scrapper.dao.service.interfaces.LinkUpdater;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
public abstract class RetryPoliticsTest {

    protected static final int ATTEMPTS_LIMIT = 3;
    protected static final int SUCCESS_RETRY_ATTEMPT_COUNT = 2;
    protected static final int REATTACH_DELTA = ATTEMPTS_LIMIT - SUCCESS_RETRY_ATTEMPT_COUNT;
    protected static final int INTERNAL_SERVER_ERROR_CODE = 500;
    protected static final int SUCCESS_CODE = 200;
    protected static WireMockServer mockServer;
    private static final String STUB_URL = "http://localhost:8080";

    @MockBean
    private ChatService chatService;
    @MockBean
    private LinkService linkService;
    @MockBean
    private LinkUpdater linkUpdater;

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
        registry.add("app.bot-settings.default-base-url", () -> STUB_URL);
        registry.add("app.stack-over-flow-settings.default-base-url", () -> STUB_URL);
        registry.add("app.git-hub-settings.default-base-url", () -> STUB_URL);
        registry.add("app.client-retry-settings.attempts-limit", () -> "3");
        registry.add("app.client-retry-settings.attempt-delay", () -> "1s");
    }

}
