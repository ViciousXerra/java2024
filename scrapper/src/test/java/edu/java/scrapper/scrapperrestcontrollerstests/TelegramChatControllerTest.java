package edu.java.scrapper.scrapperrestcontrollerstests;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.api.ratelimit.RateLimitTrackerImpl;
import edu.java.scrapper.api.restcontrollers.TelegramChatController;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TelegramChatController.class)
@Import(RateLimitTrackerImpl.class)
class TelegramChatControllerTest {

    private static final String ERROR_RESPONSE_DESC_PATH = "$.description";
    private static final String ERROR_RESPONSE_EXCEPTION_MESSAGE_PATH = "$.exceptionMessage";
    private static final String ERROR_RESPONSE_CODE_PATH = "$.code";
    private static final String ERROR_RESPONSE_EXCEPTION_NAME_PATH = "$.exceptionName";
    private static final String EXCEPTION_MESSAGE_STUB = "message";
    private static final String EXCEPTION_DESCRIPTION_STUB = "description";
    private static final String URL_PATH_VAR_NOT_LONG = "testPathVarNotLong";
    private static final String URL_TEMPLATE = "/scrapper/tg-chat/{id}";
    private static final long URL_PATH_VAR_LONG = 1L;
    @MockBean
    private ChatService chatService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test POST \"chat sign up\" 200 OK")
    void testPostSuccess() throws Exception {
        mockMvc.perform(
            post(URL_TEMPLATE, URL_PATH_VAR_LONG)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test POST \"chat sign up\" 409 Conflict")
    void testPostConflict() throws Exception {
        Mockito.doThrow(new ConflictException(EXCEPTION_MESSAGE_STUB, EXCEPTION_DESCRIPTION_STUB)).when(chatService)
            .register(URL_PATH_VAR_LONG);
        //Then
        mockMvc.perform(
                post(URL_TEMPLATE, URL_PATH_VAR_LONG)
            )
            .andExpect(status().is(409))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(ERROR_RESPONSE_DESC_PATH).value(EXCEPTION_DESCRIPTION_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_MESSAGE_PATH).value(EXCEPTION_MESSAGE_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_CODE_PATH).value("409"))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_NAME_PATH).value("ConflictException"));
    }

    @Test
    @DisplayName("Test DELETE \"chat deletion\" 200 OK")
    void testDeleteSuccess() throws Exception {
        //Then
        mockMvc.perform(
            delete(URL_TEMPLATE, URL_PATH_VAR_LONG)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test DELETE \"chat deletion\" 404 Not Found")
    void testDeleteNotFound() throws Exception {
        Mockito.doThrow(new NotFoundException(EXCEPTION_MESSAGE_STUB, EXCEPTION_DESCRIPTION_STUB)).when(chatService)
            .unregister(URL_PATH_VAR_LONG);
        //Then
        mockMvc.perform(
                delete(URL_TEMPLATE, URL_PATH_VAR_LONG)
            )
            .andExpect(status().is(404))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(ERROR_RESPONSE_DESC_PATH).value(EXCEPTION_DESCRIPTION_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_MESSAGE_PATH).value(EXCEPTION_MESSAGE_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_CODE_PATH).value("404"))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_NAME_PATH).value("NotFoundException"));
    }

    @ParameterizedTest
    @MethodSource("provideMocks")
    @DisplayName("Test POST \"chat sign up\" and DELETE \"chat deletion\" 400 BadRequest")
    void testPostBadRequest(MockHttpServletRequestBuilder builder) throws Exception {
        //Then
        mockMvc.perform(builder)
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(ERROR_RESPONSE_DESC_PATH).value("Invalid or incorrect request parameters"))
            .andExpect(jsonPath(ERROR_RESPONSE_CODE_PATH).value("400"))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_NAME_PATH).value("MethodArgumentTypeMismatchException"));
    }

    private static MockHttpServletRequestBuilder[] provideMocks() {
        return new MockHttpServletRequestBuilder[] {
            post(URL_TEMPLATE, URL_PATH_VAR_NOT_LONG),
            delete(URL_TEMPLATE, URL_PATH_VAR_NOT_LONG)
        };
    }

}
