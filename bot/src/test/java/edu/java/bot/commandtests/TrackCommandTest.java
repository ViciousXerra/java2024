package edu.java.bot.commandtests;

import edu.java.bot.commands.TrackCommand;
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

class TrackCommandTest extends CommandTest {

    @Autowired
    private TrackCommand trackCommand;

    @MockBean
    private ScrapperService scrapperService;

    @Test
    @DisplayName("Test /track command.")
    void testTrackCommand() {
        //Given
        long chatId = 1L;
        String text1 = "text";
        String text2 = "/track";
        String text3 = "/track ";
        String text4 = "/track not a link";
        String text5 = "/track https://github.com";
        String text6 = "/track https://stackoverflow.com";
        String username = "user";
        String expectedMessage123 =
            "Please, pass link after \"/track\" command. Command and link must be delimited with whitespace.";
        String expectedMessage4 =
            "The link does not satisfy the URI pattern requirements or the given resource is not supported.";
        String expectedMessage56 = "Saved.";
        String expectedMessage7 = "Already tracking";
        //When
        String actualMessage1 = trackCommand.createMessage(text1, username, chatId);
        String actualMessage2 = trackCommand.createMessage(text2, username, chatId);
        String actualMessage3 = trackCommand.createMessage(text3, username, chatId);
        String actualMessage4 = trackCommand.createMessage(text4, username, chatId);
        Mockito.doNothing().when(scrapperService).addLink(chatId, "https://github.com");
        String actualMessage5 = trackCommand.createMessage(text5, username, chatId);
        Mockito.doNothing().when(scrapperService).addLink(chatId, "https://stackoverflow.com");
        String actualMessage6 = trackCommand.createMessage(text6, username, chatId);
        Mockito.doThrow(new ClientException(new ScrapperApiErrorResponse(
            "Already tracking",
            "400",
            "exception",
            "exception_message",
            List.of("frame1", "frame2")
        ))).when(scrapperService).addLink(chatId, "https://github.com");
        String actualMessage7 = trackCommand.createMessage(text5, username, chatId);
        //Then
        Assertions.assertAll(
            () -> assertThat(trackCommand.command()).isEqualTo("/track"),
            () -> assertThat(trackCommand.description()).isEqualTo(
                "Allows you to indicate interesting links by passing link after whitespace."),
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
