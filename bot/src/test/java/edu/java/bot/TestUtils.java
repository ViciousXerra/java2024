package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import edu.java.bot.temprepo.Registry;
import edu.java.bot.users.User;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import org.mockito.Mockito;

public final class TestUtils {

    public static Command createMockCommand(
        String command,
        String desc,
        Optional<User> userOptional,
        String userName,
        long id,
        String message
    ) {
        Command mockCommand = Mockito.mock(Command.class);
        Mockito.when(mockCommand.command()).thenReturn(command);
        Mockito.when(mockCommand.description()).thenReturn(desc);
        Mockito.when(mockCommand.createMessage(userOptional, userName, id)).thenReturn(message);
        return mockCommand;
    }

    public static Update createMockUpdate(String text, String username, long chatId) {
        Message mockMessage = Mockito.mock(Message.class);
        Chat mockChat = Mockito.mock(Chat.class);
        Mockito.when(mockChat.id()).thenReturn(chatId);
        Mockito.when(mockChat.username()).thenReturn(username);
        Mockito.when(mockMessage.text()).thenReturn(text);
        Mockito.when(mockMessage.chat()).thenReturn(mockChat);
        Update mockUpdate = Mockito.mock(Update.class);
        Mockito.when(mockUpdate.message()).thenReturn(mockMessage);
        return mockUpdate;
    }

    public static Registry createRepositoryStub() {
        return Mockito.mock(Registry.class);
    }

    public static Optional<User> createUserOptionalWithEmptyList() {
        return Optional.of(new User());
    }

    public static Optional<User> createUserOptionalWithFilledList() {
        User mockUser = Mockito.mock(User.class);
        Mockito.when(mockUser.getTrackingLinks()).thenReturn(Set.of(
            URI.create("https://github.com/ViciousXerra"),
            URI.create(
                "https://stackoverflow.com/questions/35531661/using-env-variable-in-spring-boots-application-properties")
        ));
        return Optional.of(mockUser);
    }

}
