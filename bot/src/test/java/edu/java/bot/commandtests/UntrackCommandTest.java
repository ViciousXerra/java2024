package edu.java.bot.commandtests;

import edu.java.bot.commands.UntrackCommand;
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

class UntrackCommandTest extends CommandTest {

    @Autowired
    private UntrackCommand untrackCommand;

    @MockBean
    private ScrapperService scrapperService;

    @Test
    @DisplayName("Test /untrack command.")
    void testUnTrackCommand() {
        //Given
        long chatId = 1L;
        String text1 = "text";
        String text2 = "/untrack";
        String text3 = "/untrack ";
        String text4 = "/untrack not a link";
        String text5 = "/untrack https://github.com";
        String text6 = "/untrack https://stackoverflow.com";
        String username = "user";
        String expectedMessage123 =
            "Please, pass link after \"/untrack\" command. Command and link must be delimited with whitespace.";
        String expectedMessage4 =
            "The link does not satisfy the URI pattern requirements or the given resource is not supported.";
        String expectedMessage56 = "Deleted.";
        String expectedMessage7 = "Not tracking";
        //When
        String actualMessage1 = untrackCommand.createMessage(text1, username, chatId);
        String actualMessage2 = untrackCommand.createMessage(text2, username, chatId);
        String actualMessage3 = untrackCommand.createMessage(text3, username, chatId);
        String actualMessage4 = untrackCommand.createMessage(text4, username, chatId);
        Mockito.doNothing().when(scrapperService).removeLink(chatId, "https://github.com");
        String actualMessage5 = untrackCommand.createMessage(text5, username, chatId);
        Mockito.doNothing().when(scrapperService).removeLink(chatId, "https://stackoverflow.com");
        String actualMessage6 = untrackCommand.createMessage(text6, username, chatId);
        Mockito.doThrow(new ClientException(new ScrapperApiErrorResponse(
            "Not tracking",
            "400",
            "exception",
            "exception_message",
            List.of("frame1", "frame2")
        ))).when(scrapperService).removeLink(chatId, "https://github.com");
        String actualMessage7 = untrackCommand.createMessage(text5, username, chatId);
        //Then
        Assertions.assertAll(
            () -> assertThat(untrackCommand.command()).isEqualTo("/untrack"),
            () -> assertThat(untrackCommand.description()).isEqualTo(
                "Allows you to remove interesting links by passing link after whitespace."),
            () -> assertThat(actualMessage1).isEqualTo(expectedMessage123),
            () -> assertThat(actualMessage2).isEqualTo(expectedMessage123),
            () -> assertThat(actualMessage3).isEqualTo(expectedMessage123),
            () -> assertThat(actualMessage4).isEqualTo(expectedMessage4),
            () -> assertThat(actualMessage5).isEqualTo(expectedMessage56),
            () -> assertThat(actualMessage6).isEqualTo(expectedMessage56),
            () -> assertThat(actualMessage7).isEqualTo(expectedMessage7)
        );
    }

}
