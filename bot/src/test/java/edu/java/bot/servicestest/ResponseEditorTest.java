package edu.java.bot.servicestest;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.TestUtils;
import edu.java.bot.applisteners.BotInitializationListener;
import edu.java.bot.commands.Command;
import edu.java.bot.messageservices.ResponseService;
import edu.java.bot.scrapperservices.ScrapperService;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ResponseEditorTest {

    private static final long CHAT_ID = 1L;
    private static final String URL = "https://github.com";
    private static final String TRACK_TEXT = "/track https://github.com";
    private static final String UNTRACK_TEXT = "/untrack https://github.com";
    private static final String USERNAME = "user";
    private static final String TEST_DESC = "TEST_DESC";

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

    @MockBean
    private ScrapperService scrapperService;

    @Autowired
    private ResponseService responseService;

    @ParameterizedTest
    @MethodSource("provideTest")
    @DisplayName("Test response editor")
    void testResponseEditor(
        Consumer<ScrapperService> scrapperServiceConsumer,
        Update mockUpdate,
        String expectedMessageContent
    ) {
        //When
        scrapperServiceConsumer.accept(scrapperService);
        SendMessage preparedMessage = responseService.prepareResponse(mockUpdate);
        //Then
        assertThat(preparedMessage.getParameters().get("text")).isEqualTo(expectedMessageContent);
    }

    private static Object[][] provideTest() {
        return new Object[][] {
            {
                TestUtils.createResetMockConsumer(),
                TestUtils.createMockUpdate("not a valid command", USERNAME, CHAT_ID),
                "Unknown command."
            },
            {
                TestUtils.createAddChatSuccessMockConsumer(CHAT_ID),
                TestUtils.createMockUpdate("/start", USERNAME, CHAT_ID),
                "Nice to meet you, %s.".formatted(USERNAME)
            },
            {
                TestUtils.createAddChatErrorMockConsumer(TEST_DESC, CHAT_ID),
                TestUtils.createMockUpdate("/start", USERNAME, CHAT_ID),
                TEST_DESC
            },
            {
                TestUtils.createRemoveChatSuccessMockConsumer(CHAT_ID),
                TestUtils.createMockUpdate("/stop", USERNAME, CHAT_ID),
                "Thank you for using this service, %s.".formatted(USERNAME)
            },
            {
                TestUtils.createRemoveChatErrorMockConsumer(TEST_DESC, CHAT_ID),
                TestUtils.createMockUpdate("/stop", USERNAME, CHAT_ID),
                TEST_DESC
            },
            {
                TestUtils.createAddLinkSuccessMockConsumer(CHAT_ID, URL),
                TestUtils.createMockUpdate(TRACK_TEXT, USERNAME, CHAT_ID),
                "Saved."
            },
            {
                TestUtils.createAddLinkErrorMockConsumer(TEST_DESC, CHAT_ID, URL),
                TestUtils.createMockUpdate(TRACK_TEXT, USERNAME, CHAT_ID),
                TEST_DESC
            },
            {
                TestUtils.createRemoveLinkSuccessMockConsumer(CHAT_ID, URL),
                TestUtils.createMockUpdate(UNTRACK_TEXT, USERNAME, CHAT_ID),
                "Deleted."
            },
            {
                TestUtils.createRemoveLinkErrorMockConsumer(TEST_DESC, CHAT_ID, URL),
                TestUtils.createMockUpdate(UNTRACK_TEXT, USERNAME, CHAT_ID),
                TEST_DESC
            }
        };
    }

}
