package edu.java.bot.commandtests;

import edu.java.bot.commands.StartCommand;
import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperclient.dto.errorresponses.ScrapperApiErrorResponse;
import edu.java.bot.scrapperservices.ScrapperService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class StartCommandTest extends CommandTest {

    @Autowired
    private StartCommand startCommand;

    @MockBean
    private ScrapperService scrapperService;

    @Test
    @DisplayName("Test /start command.")
    void testStartCommand() {
        //Given
        long chatId = 1L;
        String text = "text";
        String username = "user";
        String expectedMessage1 = "Nice to meet you, %s.".formatted(username);
        String expectedMessage2 = "Already registered";
        //When
        Mockito.doNothing().when(scrapperService).addChat(chatId);
        String actualMessage1 = startCommand.createMessage(text, username, chatId);
        Mockito.doThrow(new ClientException(new ScrapperApiErrorResponse(
            "Already registered",
            "400",
            "exception",
            "exception_message",
            List.of("frame1", "frame2")
        ))).when(scrapperService).addChat(chatId);
        String actualMessage2 = startCommand.createMessage(text, username, chatId);
        //Then
        Assertions.assertAll(
            () -> assertThat(startCommand.command()).isEqualTo("/start"),
            () -> assertThat(startCommand.description()).isEqualTo(
                "Allows you to start using the service and subscribe for link updates."),
            () -> assertThat(actualMessage1).isEqualTo(expectedMessage1),
            () -> assertThat(actualMessage2).isEqualTo(expectedMessage2)
        );
    }

}
