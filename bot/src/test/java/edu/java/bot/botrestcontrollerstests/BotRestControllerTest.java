package edu.java.bot.botrestcontrollerstests;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.api.restcontrollers.BotRestController;
import edu.java.bot.applisteners.BotInitializationListener;
import edu.java.bot.commands.Command;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BotRestController.class)
class BotRestControllerTest {

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
    private final static String INVALID_REQUEST_BODY1 =
        """
        {
            "id": 1,
            "url": "not a link",
            "description": "desc",
            "tgChatIds": [
                1,
                2,
                3
            ]
        }
        """;
    private final static String INVALID_REQUEST_BODY2 =
        """
        {
            "id": 1,
            "url": "http://github.com",
            "description": "",
            "tgChatIds": [
                1,
                2,
                3
            ]
        }
        """;
    private final static String INVALID_REQUEST_BODY3 =
        """
        {
            "id": 1,
            "url": "http://github.com",
            "description": "desc",
            "tgChatIds": [
            ]
        }
        """;

    @MockBean
    private TelegramBot bot;

    @TestConfiguration
    static class TestingConfig {

        @Bean
        ApplicationListener<ContextRefreshedEvent> otInitializationListener(
            TelegramBot bot,
            List<Command> allSupportedCommands
        ) {
            return new BotInitializationListener(bot, allSupportedCommands);
        }

    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test POST 200 OK success")
    void testPostLinkUpdateSuccess() throws Exception {
        mockMvc.perform(
            post("/bot/updates")
                .contentType("application/json")
                .content(VALID_REQUEST_BODY)
        ).andExpect(status().isOk());
    }

    private static String[] provideBadRequestBody() {
        return new String[] {
            INVALID_REQUEST_BODY1,
            INVALID_REQUEST_BODY2,
            INVALID_REQUEST_BODY3
        };
    }

    @ParameterizedTest
    @MethodSource("provideBadRequestBody")
    @DisplayName("Test POST 400 Bad Request")
    void testPostLinkUpdateBadRequest(String badRequestBody) throws Exception {
        mockMvc.perform(
                post("/bot/updates")
                    .contentType("application/json")
                    .content(badRequestBody)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"));
    }

}
