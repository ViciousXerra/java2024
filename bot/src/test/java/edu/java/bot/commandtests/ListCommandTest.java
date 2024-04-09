package edu.java.bot.commandtests;

import edu.java.bot.commands.ListCommand;
import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperclient.dto.errorresponses.ScrapperApiErrorResponse;
import edu.java.bot.scrapperservices.ScrapperService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.Assertions.assertThat;

class ListCommandTest extends CommandTest {

    @Autowired
    private ListCommand listCommand;

    @MockBean
    private ScrapperService scrapperService;

    @Test
    @DisplayName("Test /list command.")
    void testListCommand() {
        //Given
        long chatId = 1L;
        String text = "text";
        String username = "user";
        String expectedMessage1 = "You are not tracking any links.";
        String expectedMessage2 =
            """
                link 1: https://github.com
                link 2: https://stackoverflow.com
                """;
        String expectedMessage3 = "client malfunction desc";
        //When
        Mockito.doReturn(List.of())
            .when(scrapperService).getAllLinks(chatId);
        String actualMessage1 = listCommand.createMessage(text, username, chatId);
        Mockito.doReturn(List.of(URI.create("https://github.com"), URI.create("https://stackoverflow.com")))
            .when(scrapperService).getAllLinks(chatId);
        String actualMessage2 = listCommand.createMessage(text, username, chatId);
        Mockito.doThrow(new ClientException(new ScrapperApiErrorResponse(
            "client malfunction desc",
            "400",
            "exception",
            "exception_message",
            List.of("frame1", "frame2")
        ))).when(scrapperService).getAllLinks(chatId);
        String actualMessage3 = listCommand.createMessage(text, username, chatId);
        //Then
        Assertions.assertAll(
            () -> assertThat(listCommand.command()).isEqualTo("/list"),
            () -> assertThat(listCommand.description()).isEqualTo("Shows a list of all tracking links."),
            () -> assertThat(actualMessage1).isEqualTo(expectedMessage1),
            () -> assertThat(actualMessage2.replaceAll("[\r\n]", "")).isEqualTo(expectedMessage2.replaceAll(
                "[\r\n]",
                ""
            )),
            () -> assertThat(actualMessage3).isEqualTo(expectedMessage3)
        );
    }

}
