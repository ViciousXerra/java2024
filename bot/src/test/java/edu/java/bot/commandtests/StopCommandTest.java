package edu.java.bot.commandtests;

import edu.java.bot.commands.StopCommand;
import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperclient.dto.errorresponses.ScrapperApiErrorResponse;
import edu.java.bot.scrapperservices.ScrapperService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;

public class StopCommandTest extends CommandTest {

    @Autowired
    private StopCommand stopCommand;

    @MockBean
    private ScrapperService scrapperService;

    @Test
    @DisplayName("Test /stop command.")
    void testStopCommand() {
        //Given
        long chatId = 1L;
        String text = "text";
        String username = "user";
        String expectedMessage1 = "Thank you for using this service, %s.".formatted(username);
        String expectedMessage2 = "Already unsubscribed";
        //When
        Mockito.doNothing().when(scrapperService).removeChat(chatId);
        String actualMessage1 = stopCommand.createMessage(text, username, chatId);
        Mockito.doThrow(new ClientException(new ScrapperApiErrorResponse(
            "Already unsubscribed",
            "400",
            "exception",
            "exception_message",
            List.of("frame1", "frame2")
        ))).when(scrapperService).removeChat(chatId);
        String actualMessage2 = stopCommand.createMessage(text, username, chatId);
        //Then
        Assertions.assertAll(
            () -> assertThat(stopCommand.command()).isEqualTo("/stop"),
            () -> assertThat(stopCommand.description()).isEqualTo(
                "Allows you to stop using the service and unsubscribe from link updates."),
            () -> assertThat(actualMessage1).isEqualTo(expectedMessage1),
            () -> assertThat(actualMessage2).isEqualTo(expectedMessage2)
        );
    }

}
