package edu.java.bot.commandtests;

import edu.java.bot.TestUtils;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.users.User;
import edu.java.bot.users.UserChatCondition;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UntrackCommandTest {

    private static Optional<User> emptyUserOptional;
    private static Optional<User> presentUserOptional;

    @BeforeAll
    public static void setup() {
        emptyUserOptional = Optional.empty();
        presentUserOptional = TestUtils.createUserOptionalWithEmptyList();
    }

    @Test
    @DisplayName("Test /untrack command.")
    void testUntrackCommand() {
        //Given
        Command untrackCommand = new UntrackCommand();
        //When
        String actualCommand = untrackCommand.command();
        String actualDescription = untrackCommand.description();
        String actualNewUserMessage = untrackCommand.createMessage(emptyUserOptional, "username1", 1L);
        String actualDefaultConditionMessage = untrackCommand.createMessage(presentUserOptional, "username2", 2L);
        String actualAwaitingToUntrackMessage = untrackCommand.createMessage(presentUserOptional, "username2", 2L);
        //Setting to TRACK condition
        presentUserOptional.get().setCondition(UserChatCondition.AWAITING_LINK_TO_TRACK);
        String actualAwaitingToTrackMessage = untrackCommand.createMessage(presentUserOptional, "username2", 2L);
        //Then
        Assertions.assertAll(
            () -> assertThat(actualCommand)
                .isEqualTo("/untrack"),
            () -> assertThat(actualDescription)
                .isEqualTo("Allows you to remove interesting links by next message."),
            () -> assertThat(actualNewUserMessage)
                .isEqualTo("First you need to register by entering the command /start."),
            () -> assertThat(actualDefaultConditionMessage)
                .isEqualTo("Waiting for a link to be entered."),
            () -> assertThat(actualAwaitingToUntrackMessage)
                .isEqualTo("The link to delete is already expected."),
            () -> assertThat(actualAwaitingToTrackMessage)
                .isEqualTo("OK, now the link sent in the next message will be deleted.")
        );
    }

}
