package edu.java.bot.commandtests;

import com.pengrad.telegrambot.model.Update;
import edu.java.bot.TestUtils;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.users.User;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ListCommandTest {

    private static Optional<User> emptyUserOptional;
    private static Optional<User> userOptionalWithEmptyList;
    private static Optional<User> userOptionalWithFilledList;
    private static Update mockUpdate;

    @BeforeAll
    public static void setup() {
        emptyUserOptional = Optional.empty();
        userOptionalWithEmptyList = TestUtils.createUserOptionalWithEmptyList();
        userOptionalWithFilledList = TestUtils.createUserOptionalWithFilledList();
        mockUpdate = TestUtils.createMockUpdate("/list", "username", 0L);
    }

    @Test
    @DisplayName("Test /list command.")
    void testListCommand() {
        //Given
        Command listCommand = new ListCommand();
        //When
        String actualCommand = listCommand.command();
        String actualDescription = listCommand.description();
        String actualEmptyUserOptionalMessage =
            listCommand.createMessage(emptyUserOptional, "userName1", 1L);
        String actualUserOptionalWithEmptyListMessage =
            listCommand.createMessage(userOptionalWithEmptyList, "userName2", 2L);
        String actualUserOptionalWithFilledListMessage =
            listCommand.createMessage(userOptionalWithFilledList, "userName3", 3L);
        boolean actualSupports = listCommand.isSupport(mockUpdate);
        //Then
        Assertions.assertAll(
            () -> assertThat(actualCommand).isEqualTo("/list"),
            () -> assertThat(actualDescription).isEqualTo("Shows a list of all tracking links."),
            () -> assertThat(actualEmptyUserOptionalMessage)
                .isEqualTo("First you need to register by entering the command /start."),
            () -> assertThat(actualUserOptionalWithEmptyListMessage)
                .isEqualTo("You are not tracking any links."),
            () -> assertThat(actualUserOptionalWithFilledListMessage)
                .contains(
                    "https://github.com/ViciousXerra",
                    "https://stackoverflow.com/questions/35531661/using-env-variable-in-spring-boots-application-properties"
                ),
            () -> assertThat(actualSupports).isTrue()
        );
    }

}
