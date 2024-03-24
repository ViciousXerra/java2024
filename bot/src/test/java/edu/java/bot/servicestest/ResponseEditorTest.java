package edu.java.bot.servicestest;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.TestUtils;
import edu.java.bot.applisteners.BotInitializationListener;
import edu.java.bot.commands.Command;
import edu.java.bot.messageservices.ResponseService;
import java.util.List;
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
/*
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

    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Autowired
    private ResponseService responseServiceBean;

    private static Object[][] provideTestWithData() {
        return new Object[][] {
            {
                "First you need to register by entering the command /start.",
                TestUtils.createMockUpdate("/list", "username", 1L)
            },
            {
                "First you need to register by entering the command /start.",
                TestUtils.createMockUpdate("/track", "username", 1L)
            },
            {
                "First you need to register by entering the command /start.",
                TestUtils.createMockUpdate("/untrack", "username", 1L)
            },
            {
                "First you need to register by entering the command /start.",
                TestUtils.createMockUpdate("/track", "username", 1L)
            },
            {
                "Nice to meet you, username.",
                TestUtils.createMockUpdate("/start", "username", 1L)
            },
            {
                "Hello again, username. Shall we continue?",
                TestUtils.createMockUpdate("/start", "username", 1L)
            },
            {
                "Waiting for a link to be entered.",
                TestUtils.createMockUpdate("/track", "username", 1L)
            },
            {
                "The link to save is already expected.",
                TestUtils.createMockUpdate("/track", "username", 1L)
            },
            {
                "OK, now the link sent in the next message will be deleted.",
                TestUtils.createMockUpdate("/untrack", "username", 1L)
            },
            {
                "The link to delete is already expected.",
                TestUtils.createMockUpdate("/untrack", "username", 1L)
            },
            {
                "OK, now the link sent in the next message will be saved.",
                TestUtils.createMockUpdate("/track", "username", 1L)
            },
            {
                "The link was successfully saved.",
                TestUtils.createMockUpdate("https://github.com/ViciousXerra", "username", 1L)
            },
            {
                "Invalid command.",
                TestUtils.createMockUpdate(
                    "https://stackoverflow.com/questions/35531661/using-env-variable-in-spring-boots-application-properties",
                    "username",
                    1L
                )
            },
            {
                "Waiting for a link to be entered.",
                TestUtils.createMockUpdate("/untrack", "username", 1L)
            },
            {
                "The link was successfully deleted.",
                TestUtils.createMockUpdate("https://github.com/ViciousXerra", "username", 1L)
            },
            {
                "You are not tracking any links.",
                TestUtils.createMockUpdate("/list", "username", 1L)
            },
            {
                "Waiting for a link to be entered.",
                TestUtils.createMockUpdate("/track", "username", 1L)
            },
            {
                "The link was successfully saved.",
                TestUtils.createMockUpdate("https://github.com/ViciousXerra", "username", 1L)
            },
            {
                "Waiting for a link to be entered.",
                TestUtils.createMockUpdate("/track", "username", 1L)
            },
            {
                "The link was successfully saved.",
                TestUtils.createMockUpdate(
                    "https://stackoverflow.com/questions/35531661/using-env-variable-in-spring-boots-application-properties",
                    "username",
                    1L
                )
            },
            {
                "Waiting for a link to be entered.",
                TestUtils.createMockUpdate("/track", "username", 1L)
            },
            {
                "The link is not in the correct format or the resource is not supported.",
                TestUtils.createMockUpdate(
                    "https://javarush.com/login",
                    "username",
                    1L
                )
            },
            {
                "https://github.com/ViciousXerra" +
                LINE_SEPARATOR +
                LINE_SEPARATOR +
                "https://stackoverflow.com/questions/35531661/using-env-variable-in-spring-boots-application-properties" +
                LINE_SEPARATOR +
                LINE_SEPARATOR,
                TestUtils.createMockUpdate("/list", "username", 1L)
            },
        };
    }

    @ParameterizedTest
    @DisplayName("Test response editor.")
    @MethodSource("provideTestWithData")
    void testResponseEditor(String expectedOutput, Update update) {
        //When
        SendMessage actualOutputObj = responseServiceBean.prepareResponse(update);
        String actualOutputString = (String) actualOutputObj.getParameters().get("text");
        //Then
        assertThat(actualOutputString).isEqualTo(expectedOutput);
    }
*/
}
