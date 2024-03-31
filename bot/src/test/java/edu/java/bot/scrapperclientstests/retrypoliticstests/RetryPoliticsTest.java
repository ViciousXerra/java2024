package edu.java.bot.scrapperclientstests.retrypoliticstests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
public abstract class RetryPoliticsTest {

    protected static WireMockServer mockServer;

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
