package edu.java.bot.commandtests;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.TestUtils;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class HelpCommandTest {

    private static Command mockCommand1;

    private static Command mockCommand2;
    private static Update mockUpdate;

    @BeforeAll
    public static void setup() {
        mockCommand1 =
            TestUtils.createMockCommand("mockCommand1", "mockDesc1",
                Optional.empty(), "mockUserName1", 1L, "mockMessage1"
            );
        mockCommand2 = TestUtils.createMockCommand("mockCommand2", "mockDesc2",
            Optional.empty(), "mockUserName2", 2L, "mockMessage2"
        );
        mockUpdate = TestUtils.createMockUpdate("/help", "username", 0L);
    }

    @Test
    @DisplayName("Test /help command.")
    void testHelpCommand() {
        //Given
        Command helpCommand = new HelpCommand(List.of(mockCommand1, mockCommand2));
        //When
        String actualCommand = helpCommand.command();
        String actualDescription = helpCommand.description();
        String actualMessage = helpCommand.createMessage(Optional.empty(), "user", 1L);
        boolean actualSupports = helpCommand.supports(mockUpdate);
        //Then
        Assertions.assertAll(
            () -> assertThat(actualCommand).isEqualTo("/help"),
            () -> assertThat(actualDescription).isEqualTo("Shows a list of all commands."),
            () -> assertThat(actualMessage).contains("mockCommand1: mockDesc1", "mockCommand2: mockDesc2"),
            () -> assertThat(actualSupports).isTrue()
        );
    }

}
