package edu.java.bot.commandtests;

import edu.java.bot.commands.HelpCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

class HelpCommandTest extends CommandTest {

    private static final String COMMAND_DESC_TEMPLATE = "%s: %s";

    @Autowired
    private HelpCommand helpCommand;

    @Test
    @DisplayName("Test /help command.")
    void testHelpCommand() {
        //Then
        Assertions.assertAll(
            () -> assertThat(helpCommand.command()).isEqualTo("/help"),
            () -> assertThat(helpCommand.description()).isEqualTo("Shows a list of all commands."),
            () -> assertThat(helpCommand.createMessage("text", "user", 1L)).contains(
                COMMAND_DESC_TEMPLATE.formatted("/list", "Shows a list of all tracking links."),
                COMMAND_DESC_TEMPLATE.formatted(
                    "/start",
                    "Allows you to start using the service and subscribe for link updates."
                ),
                COMMAND_DESC_TEMPLATE.formatted(
                    "/stop",
                    "Allows you to stop using the service and unsubscribe from link updates."
                ),
                COMMAND_DESC_TEMPLATE.formatted(
                    "/track",
                    "Allows you to indicate interesting links by passing link after whitespace."
                ),
                COMMAND_DESC_TEMPLATE.formatted(
                    "/untrack",
                    "Allows you to remove interesting links by passing link after whitespace."
                )
            )
        );
    }

}
