package edu.java.scrapper.scrapperrestcontrollerstests;

import edu.java.scrapper.api.exceptions.ConflictException;
import edu.java.scrapper.api.exceptions.NotFoundException;
import edu.java.scrapper.api.ratelimit.RateLimitTrackerImpl;
import edu.java.scrapper.api.restcontrollers.LinksRestController;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import java.time.ZonedDateTime;
import java.util.List;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinksRestController.class)
@Import(RateLimitTrackerImpl.class)
class LinksRestControllerTest {

    private static final String URL_TEMPLATE = "/scrapper/links";
    private static final String HEADER_LABEL = "Tg-Chat-Id";
    private static final long HEADER_VALID_VALUE = 1L;
    private static final String HEADER_INVALID_VALUE = "notLong";
    private static final String VALID_LINK = "https://stackoverflow.com";
    private static final long LINK_ID1 = 1L;
    private static final String LINKS_PATH = "$.links";
    private static final String SIZE_PATH = "$.size";
    private static final String LINK_ID_PATH = "$.id";
    private static final String URL_PATH = "$.url";
    private static final String ERROR_RESPONSE_DESC_PATH = "$.description";
    private static final String ERROR_RESPONSE_EXCEPTION_MESSAGE_PATH = "$.exceptionMessage";
    private static final String ERROR_RESPONSE_CODE_PATH = "$.code";
    private static final String ERROR_RESPONSE_EXCEPTION_NAME_PATH = "$.exceptionName";
    private static final String BAD_REQUEST_DESC = "Invalid or incorrect request parameters";
    private static final String BAD_REQUEST_CODE = "400";
    private static final String NOT_FOUND_CODE = "404";
    private static final String EXCEPTION_MESSAGE_STUB = "message";
    private static final String EXCEPTION_DESCRIPTION_STUB = "description";
    private static final String NOT_FOUND_EXCEPTION_SIMPLE_NAME = "NotFoundException";
    private static final String CONFLICT_EXCEPTION_SIMPLE_NAME = "ConflictException";
    private static final String LINK_REQUEST_VALID_CONTENT_BODY =
            """
            {
                "link": "https://stackoverflow.com"
            }
            """;
    private static final String LINK_REQUEST_INVALID_CONTENT_BODY =
            """
            {
                "link": "not a valid link"
            }
            """;

    @MockBean
    private LinkService linkService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test GET \"list all\" 200 OK")
    void testGetSuccess() throws Exception {
        List<Link> returnStubCollection =
            List.of(new Link(LINK_ID1, VALID_LINK, ZonedDateTime.now(), ZonedDateTime.now()));
        Mockito.doReturn(returnStubCollection).when(linkService)
            .listAll(HEADER_VALID_VALUE);
        //Then
        mockMvc.perform(get(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(SIZE_PATH).value(returnStubCollection.size()))
            .andExpect(jsonPath(LINKS_PATH).isArray())
            .andExpect(jsonPath(LINKS_PATH).isNotEmpty());
    }

    @Test
    @DisplayName("Test POST \"add link\" 200 OK")
    void testPostSuccess() throws Exception {
        Mockito.doReturn(new Link(LINK_ID1, VALID_LINK, ZonedDateTime.now(), ZonedDateTime.now())).when(linkService)
            .add(HEADER_VALID_VALUE, VALID_LINK);
        //Then
        mockMvc.perform(post(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE)
                .contentType(MediaType.APPLICATION_JSON).content(LINK_REQUEST_VALID_CONTENT_BODY))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(LINK_ID_PATH).value(LINK_ID1))
            .andExpect(jsonPath(URL_PATH).value(VALID_LINK));
    }

    @Test
    @DisplayName("Test DELETE \"remove link\" 200 OK")
    void testDeleteSuccess() throws Exception {
        Mockito.doReturn(new Link(LINK_ID1, VALID_LINK, ZonedDateTime.now(), ZonedDateTime.now())).when(linkService)
            .remove(HEADER_VALID_VALUE, VALID_LINK);
        //Then
        mockMvc.perform(delete(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE)
                .contentType(MediaType.APPLICATION_JSON).content(LINK_REQUEST_VALID_CONTENT_BODY))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(LINK_ID_PATH).value(LINK_ID1))
            .andExpect(jsonPath(URL_PATH).value(VALID_LINK));
    }

    @Test
    @DisplayName("Test POST \"add link\" 409 Conflict")
    void testPostConflict() throws Exception {
        Mockito.doThrow(new ConflictException(EXCEPTION_MESSAGE_STUB, EXCEPTION_DESCRIPTION_STUB)).when(linkService)
            .add(HEADER_VALID_VALUE, VALID_LINK);
        //Then
        mockMvc.perform(post(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE)
                .contentType(MediaType.APPLICATION_JSON).content(LINK_REQUEST_VALID_CONTENT_BODY))
            .andExpect(status().is(409))
            .andExpect(jsonPath(ERROR_RESPONSE_DESC_PATH).value(EXCEPTION_DESCRIPTION_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_MESSAGE_PATH).value(EXCEPTION_MESSAGE_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_CODE_PATH).value("409"))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_NAME_PATH).value(CONFLICT_EXCEPTION_SIMPLE_NAME));
    }

    @Test
    @DisplayName("Test GET \"list all\" 404 NotFound")
    void testGetNotFound() throws Exception {
        Mockito.doThrow(new NotFoundException(EXCEPTION_MESSAGE_STUB, EXCEPTION_DESCRIPTION_STUB)).when(linkService)
            .listAll(HEADER_VALID_VALUE);
        //Then
        mockMvc.perform(get(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(ERROR_RESPONSE_DESC_PATH).value(EXCEPTION_DESCRIPTION_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_MESSAGE_PATH).value(EXCEPTION_MESSAGE_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_CODE_PATH).value(NOT_FOUND_CODE))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_NAME_PATH).value(NOT_FOUND_EXCEPTION_SIMPLE_NAME));
    }

    @Test
    @DisplayName("Test POST \"add link\" 404 NotFound")
    void testPostNotFound() throws Exception {
        Mockito.doThrow(new NotFoundException(EXCEPTION_MESSAGE_STUB, EXCEPTION_DESCRIPTION_STUB)).when(linkService)
            .add(HEADER_VALID_VALUE, VALID_LINK);
        //Then
        mockMvc.perform(
                post(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE).contentType(MediaType.APPLICATION_JSON)
                    .content(LINK_REQUEST_VALID_CONTENT_BODY)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(ERROR_RESPONSE_DESC_PATH).value(EXCEPTION_DESCRIPTION_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_MESSAGE_PATH).value(EXCEPTION_MESSAGE_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_CODE_PATH).value(NOT_FOUND_CODE))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_NAME_PATH).value(NOT_FOUND_EXCEPTION_SIMPLE_NAME));
    }

    @Test
    @DisplayName("Test Delete \"remove link\" 404 NotFound")
    void testDeleteNotFound() throws Exception {
        Mockito.doThrow(new NotFoundException(EXCEPTION_MESSAGE_STUB, EXCEPTION_DESCRIPTION_STUB)).when(linkService)
            .remove(HEADER_VALID_VALUE, VALID_LINK);
        //Then
        mockMvc.perform(
                delete(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE).contentType(MediaType.APPLICATION_JSON)
                    .content(LINK_REQUEST_VALID_CONTENT_BODY)
            )
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(ERROR_RESPONSE_DESC_PATH).value(EXCEPTION_DESCRIPTION_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_MESSAGE_PATH).value(EXCEPTION_MESSAGE_STUB))
            .andExpect(jsonPath(ERROR_RESPONSE_CODE_PATH).value(NOT_FOUND_CODE))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_NAME_PATH).value(NOT_FOUND_EXCEPTION_SIMPLE_NAME));
    }

    @ParameterizedTest
    @MethodSource("provideMocksWithArgumentMismatchInHeader")
    @DisplayName("Test GET, POST, DELETE 400 BadRequest (argument mismatch)")
    void testGetPostDeleteBadRequestArgumentMismatch(MockHttpServletRequestBuilder builder) throws Exception {
        //Then
        mockMvc.perform(builder)
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(ERROR_RESPONSE_DESC_PATH).value(BAD_REQUEST_DESC))
            .andExpect(jsonPath(ERROR_RESPONSE_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_NAME_PATH).value("MethodArgumentTypeMismatchException"));
    }

    @ParameterizedTest
    @MethodSource("provideMocksWithMissingHeader")
    @DisplayName("Test GET, POST, DELETE 400 BadRequest (missing header)")
    void testGetPostDeleteBadRequestMissingHeader(MockHttpServletRequestBuilder builder) throws Exception {
        //Then
        mockMvc.perform(builder)
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(ERROR_RESPONSE_DESC_PATH).value(BAD_REQUEST_DESC))
            .andExpect(jsonPath(ERROR_RESPONSE_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_NAME_PATH).value("MissingRequestHeaderException"));
    }

    @ParameterizedTest
    @MethodSource("provideMocksWithInvalidRequestBody")
    @DisplayName("Test GET, POST, DELETE 400 BadRequest (invalid request body)")
    void testGetPostDeleteBadRequestInvalidRequestBody(MockHttpServletRequestBuilder builder) throws Exception {
        //Then
        mockMvc.perform(builder)
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(ERROR_RESPONSE_DESC_PATH).value(BAD_REQUEST_DESC))
            .andExpect(jsonPath(ERROR_RESPONSE_CODE_PATH).value(BAD_REQUEST_CODE))
            .andExpect(jsonPath(ERROR_RESPONSE_EXCEPTION_NAME_PATH).value("MethodArgumentNotValidException"));
    }

    private static MockHttpServletRequestBuilder[] provideMocksWithArgumentMismatchInHeader() {
        return new MockHttpServletRequestBuilder[] {
            get(URL_TEMPLATE).header(HEADER_LABEL, HEADER_INVALID_VALUE),
            post(URL_TEMPLATE).header(HEADER_LABEL, HEADER_INVALID_VALUE)
                .contentType(MediaType.APPLICATION_JSON).content(
                LINK_REQUEST_VALID_CONTENT_BODY
            ),
            delete(URL_TEMPLATE).header(HEADER_LABEL, HEADER_INVALID_VALUE)
                .contentType(MediaType.APPLICATION_JSON).content(
                LINK_REQUEST_VALID_CONTENT_BODY
            )
        };
    }

    private static MockHttpServletRequestBuilder[] provideMocksWithMissingHeader() {
        return new MockHttpServletRequestBuilder[] {
            get(URL_TEMPLATE),
            post(URL_TEMPLATE).contentType(MediaType.APPLICATION_JSON).content(
                LINK_REQUEST_VALID_CONTENT_BODY
            ),
            delete(URL_TEMPLATE).contentType(MediaType.APPLICATION_JSON).content(
                LINK_REQUEST_VALID_CONTENT_BODY
            )
        };
    }

    private static MockHttpServletRequestBuilder[] provideMocksWithInvalidRequestBody() {
        return new MockHttpServletRequestBuilder[] {
            post(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE).contentType(MediaType.APPLICATION_JSON).content(
                LINK_REQUEST_INVALID_CONTENT_BODY
            ),
            delete(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE)
                .contentType(MediaType.APPLICATION_JSON).content(
                LINK_REQUEST_INVALID_CONTENT_BODY
            )
        };
    }

}
