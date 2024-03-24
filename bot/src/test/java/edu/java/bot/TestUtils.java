package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.commands.Command;
import java.net.URI;
import java.util.Optional;
import java.util.Set;
import org.mockito.Mockito;

public final class TestUtils {
/*
    public static Command createMockCommand(
        String command,
        String desc,
        String userName,
        long id,
        String message
    ) {
        Command mockCommand = Mockito.mock(Command.class);
        Mockito.when(mockCommand.command()).thenReturn(command);
        Mockito.when(mockCommand.description()).thenReturn(desc);
        Mockito.when(mockCommand.createMessage().thenReturn(message);
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
*/
}
