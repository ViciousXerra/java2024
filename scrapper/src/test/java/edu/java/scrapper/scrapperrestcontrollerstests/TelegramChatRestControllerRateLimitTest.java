package edu.java.scrapper.scrapperrestcontrollerstests;

import edu.java.scrapper.api.ratelimit.RateLimitTrackerImpl;
import edu.java.scrapper.api.restcontrollers.TelegramChatController;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TelegramChatController.class)
@Import(RateLimitTrackerImpl.class)
class TelegramChatRestControllerRateLimitTest {

    private static final String URL_TEMPLATE = "/scrapper/tg-chat/{id}";
    private static final long URL_PATH_VAR_LONG = 1L;
    private static final int REQUEST_QUOTA = 5;
    private static final long THREAD_SLEEP_MS = 10000;
    @MockBean
    private ChatService chatService;
    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void stubClientSettings(DynamicPropertyRegistry registry) {
        registry.add("app.api-rate-limit-settings.limit", () -> "5");
        registry.add("app.api-rate-limit-settings.refill-limit", () -> "5");
        registry.add("app.api-rate-limit-settings.refill-delay", () -> "5s");
    }

    @AfterEach
    public void waitAfterEachTest() throws InterruptedException {
        Thread.sleep(THREAD_SLEEP_MS);
    }

    @Test
    @DisplayName("Test POST endpoint rate limit")
    void testPostEndpointRateLimit() throws Exception {
        //Successful responses
        for (int i = 0; i < REQUEST_QUOTA; i++) {
            mockMvc.perform(
                post(URL_TEMPLATE, URL_PATH_VAR_LONG)
            ).andExpect(status().isOk());
        }
        //Quota exhausted
        mockMvc.perform(
            post(URL_TEMPLATE, URL_PATH_VAR_LONG)
        ).andExpect(status().isTooManyRequests());
        //Quota restored
        Thread.sleep(THREAD_SLEEP_MS);
        //Successful response
        mockMvc.perform(
            post(URL_TEMPLATE, URL_PATH_VAR_LONG)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test DELETE endpoint rate limit")
    void testDeleteEndpointRateLimit() throws Exception {
        //Successful responses
        for (int i = 0; i < REQUEST_QUOTA; i++) {
            mockMvc.perform(
                delete(URL_TEMPLATE, URL_PATH_VAR_LONG)
            ).andExpect(status().isOk());
        }
        //Quota exhausted
        mockMvc.perform(
            delete(URL_TEMPLATE, URL_PATH_VAR_LONG)
        ).andExpect(status().isTooManyRequests());
        //Quota restored
        Thread.sleep(THREAD_SLEEP_MS);
        //Successful response
        mockMvc.perform(
            delete(URL_TEMPLATE, URL_PATH_VAR_LONG)
        ).andExpect(status().isOk());
    }

}
