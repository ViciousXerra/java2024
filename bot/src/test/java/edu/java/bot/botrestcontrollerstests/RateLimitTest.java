package edu.java.bot.botrestcontrollerstests;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.api.ratelimit.RateLimitTrackerImpl;
import edu.java.bot.api.restcontrollers.BotRestController;
import edu.java.bot.applisteners.BotInitializationListener;
import edu.java.bot.commands.Command;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BotRestController.class)
class RateLimitTest {

    private final static String VALID_REQUEST_BODY =
        """
        {
            "id": 1,
            "url": "https://github.com",
            "description": "desc",
            "tgChatIds": [
                1,
                2,
                3
            ]
        }
        """;
    private static final String MEDIA_TYPE = "application/json";
    private static final String URL_PATH = "/bot/updates";
    private static final int REQUEST_QUOTA = 5;
    private static final long THREAD_SLEEP_MS = 10000;

    @MockBean
    private TelegramBot bot;

    @TestConfiguration
    @Import(RateLimitTrackerImpl.class)
    static class TestingConfig {

        @Bean
        ApplicationListener<ContextRefreshedEvent> botInitializationListener(
            TelegramBot bot,
            List<Command> allSupportedCommands
        ) {
            return new BotInitializationListener(bot, allSupportedCommands);
        }

    }

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void stubClientSettings(DynamicPropertyRegistry registry) {
        registry.add("app.api-rate-limit-settings.limit", () -> "5");
        registry.add("app.api-rate-limit-settings.refill-limit", () -> "5");
        registry.add("app.api-rate-limit-settings.refill-delay", () -> "5s");
    }

    @Test
    @DisplayName("Test POST 200 OK success")
    void testPostLinkUpdateSuccess() throws Exception {
        //Successful responses
        for (int i = 0; i < REQUEST_QUOTA; i++) {
            mockMvc.perform(
                post(URL_PATH)
                    .contentType(MEDIA_TYPE)
                    .content(VALID_REQUEST_BODY)
            ).andExpect(status().isOk());
        }
        //Quota exhausted
        mockMvc.perform(
            post(URL_PATH)
                .contentType(MEDIA_TYPE)
                .content(VALID_REQUEST_BODY)
        )
            .andExpect(status().isTooManyRequests())
            .andExpect(content().contentType(MEDIA_TYPE));
        //Quota restored
        Thread.sleep(THREAD_SLEEP_MS);
        //Successful response
        mockMvc.perform(
            post(URL_PATH)
                .contentType(MEDIA_TYPE)
                .content(VALID_REQUEST_BODY)
        ).andExpect(status().isOk());
    }

}
