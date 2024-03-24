package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import edu.java.bot.scrapperclient.ClientException;
import edu.java.bot.scrapperclient.dto.errorresponses.ScrapperApiErrorResponse;
import edu.java.bot.scrapperservices.ScrapperService;
import java.util.List;
import java.util.function.Consumer;
import org.mockito.Mockito;

public final class TestUtils {

    private static final String BAD_REQUEST_CODE = "400";
    private static final String EXCEPTION_NAME = "exception_name";
    private static final String EXCEPTION_MESSAGE = "exception_message";
    private static final String FRAME_1 = "frame1";
    private static final String FRAME_2 = "frame2";

    public static Consumer<ScrapperService> createAddLinkErrorMockConsumer(String desc, long chatId, String text) {
        return scrapperService -> Mockito.doThrow(new ClientException(
            new ScrapperApiErrorResponse(
                desc,
                BAD_REQUEST_CODE,
                EXCEPTION_NAME,
                EXCEPTION_MESSAGE,
                List.of(FRAME_1, FRAME_2)
            )
        )).when(scrapperService).addLink(chatId, text);
    }

    public static Consumer<ScrapperService> createAddLinkSuccessMockConsumer(long chatId, String text) {
        return scrapperService -> Mockito.doNothing().when(scrapperService).addLink(chatId, text);
    }

    public static Consumer<ScrapperService> createRemoveLinkErrorMockConsumer(String desc, long chatId, String text) {
        return scrapperService -> Mockito.doThrow(new ClientException(
            new ScrapperApiErrorResponse(
                desc,
                BAD_REQUEST_CODE,
                EXCEPTION_NAME,
                EXCEPTION_MESSAGE,
                List.of(FRAME_1, FRAME_2)
            )
        )).when(scrapperService).removeLink(chatId, text);
    }

    public static Consumer<ScrapperService> createRemoveLinkSuccessMockConsumer(long chatId, String text) {
        return scrapperService -> Mockito.doNothing().when(scrapperService).removeLink(chatId, text);
    }

    public static Consumer<ScrapperService> createAddChatSuccessMockConsumer(long chatId) {
        return scrapperService -> Mockito.doNothing().when(scrapperService).addChat(chatId);
    }

    public static Consumer<ScrapperService> createAddChatErrorMockConsumer(String desc, long chatId) {
        return scrapperService -> Mockito.doThrow(new ClientException(
            new ScrapperApiErrorResponse(
                desc,
                BAD_REQUEST_CODE,
                EXCEPTION_NAME,
                EXCEPTION_MESSAGE,
                List.of(FRAME_1, FRAME_2)
            )
        )).when(scrapperService).addChat(chatId);
    }

    public static Consumer<ScrapperService> createRemoveChatErrorMockConsumer(String desc, long chatId) {
        return scrapperService -> Mockito.doThrow(new ClientException(
            new ScrapperApiErrorResponse(
                desc,
                BAD_REQUEST_CODE,
                EXCEPTION_NAME,
                EXCEPTION_MESSAGE,
                List.of(FRAME_1, FRAME_2)
            )
        )).when(scrapperService).removeChat(chatId);
    }

    public static Consumer<ScrapperService> createRemoveChatSuccessMockConsumer(long chatId) {
        return scrapperService -> Mockito.doNothing().when(scrapperService).removeChat(chatId);
    }

    public static Consumer<ScrapperService> createResetMockConsumer() {
        return Mockito::reset;
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

}
