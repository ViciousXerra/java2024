package edu.java.scrapper.scrapperrestcontrollerstests;

import edu.java.scrapper.api.ratelimit.RateLimitTrackerImpl;
import edu.java.scrapper.api.restcontrollers.LinksRestController;
import edu.java.scrapper.dao.dto.Link;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinksRestController.class)
@Import(RateLimitTrackerImpl.class)
class LinksRestControllerRateLimitTest {

    private static final String URL_TEMPLATE = "/scrapper/links";
    private static final String HEADER_LABEL = "Tg-Chat-Id";
    private static final long HEADER_VALID_VALUE = 1L;
    private static final String VALID_LINK = "https://stackoverflow.com";
    private static final long LINK_ID1 = 1L;
    private static final String LINKS_PATH = "$.links";
    private static final String SIZE_PATH = "$.size";
    private static final String LINK_ID_PATH = "$.id";
    private static final String URL_PATH = "$.url";
    private static final String LINK_REQUEST_VALID_CONTENT_BODY =
        """
            {
                "link": "https://stackoverflow.com"
            }
            """;
    private static final int REQUEST_QUOTA = 5;
    private static final long THREAD_SLEEP_MS = 10000;
    @MockBean
    private LinkService linkService;
    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void stubClientSettings(DynamicPropertyRegistry registry) {
        registry.add("app.api-rate-limit-settings.limit", () -> "5");
        registry.add("app.api-rate-limit-settings.refill-limit", () -> "5");
        registry.add("app.api-rate-limit-settings.refill-delay", () -> "5s");
    }

    @AfterEach
    public void waitAfterEachTest() throws InterruptedException {
        Thread.sleep(THREAD_SLEEP_MS);
    }

    @Test
    @DisplayName("Test GET endpoint rate limit")
    void testGetEndpointRateLimit() throws Exception {
        //Mock
        List<Link> returnStubCollection =
            List.of(new Link(LINK_ID1, VALID_LINK, ZonedDateTime.now(), ZonedDateTime.now()));
        Mockito.doReturn(returnStubCollection).when(linkService)
            .listAll(HEADER_VALID_VALUE);
        //Successful responses
        for (int i = 0; i < REQUEST_QUOTA; i++) {
            mockMvc.perform(get(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(SIZE_PATH).value(returnStubCollection.size()))
                .andExpect(jsonPath(LINKS_PATH).isArray())
                .andExpect(jsonPath(LINKS_PATH).isNotEmpty());
        }
        //Quota exhausted
        mockMvc.perform(get(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE))
            .andExpect(status().isTooManyRequests());
        //Quota restored
        Thread.sleep(THREAD_SLEEP_MS);
        //Successful response
        mockMvc.perform(get(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(SIZE_PATH).value(returnStubCollection.size()))
            .andExpect(jsonPath(LINKS_PATH).isArray())
            .andExpect(jsonPath(LINKS_PATH).isNotEmpty());
    }

    @Test
    @DisplayName("Test POST endpoint rate limit")
    void testPostEndpointRateLimit() throws Exception {
        //Mock
        Mockito.doReturn(new Link(LINK_ID1, VALID_LINK, ZonedDateTime.now(), ZonedDateTime.now())).when(linkService)
            .add(HEADER_VALID_VALUE, VALID_LINK);
        //Successful responses
        for (int i = 0; i < REQUEST_QUOTA; i++) {
            mockMvc.perform(post(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE)
                    .contentType(MediaType.APPLICATION_JSON).content(LINK_REQUEST_VALID_CONTENT_BODY))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(LINK_ID_PATH).value(LINK_ID1))
                .andExpect(jsonPath(URL_PATH).value(VALID_LINK));
        }
        //Quota exhausted
        mockMvc.perform(post(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE)
                .contentType(MediaType.APPLICATION_JSON).content(LINK_REQUEST_VALID_CONTENT_BODY))
            .andExpect(status().isTooManyRequests());
        //Quota restored
        Thread.sleep(THREAD_SLEEP_MS);
        //Successful response
        mockMvc.perform(post(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE)
                .contentType(MediaType.APPLICATION_JSON).content(LINK_REQUEST_VALID_CONTENT_BODY))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(LINK_ID_PATH).value(LINK_ID1))
            .andExpect(jsonPath(URL_PATH).value(VALID_LINK));
    }

    @Test
    @DisplayName("Test DELETE endpoint rate limit")
    void testDeleteEndpointRateLimit() throws Exception {
        //Mock
        Mockito.doReturn(new Link(LINK_ID1, VALID_LINK, ZonedDateTime.now(), ZonedDateTime.now())).when(linkService)
            .remove(HEADER_VALID_VALUE, VALID_LINK);
        //Successful responses
        for (int i = 0; i < REQUEST_QUOTA; i++) {
            mockMvc.perform(delete(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE)
                    .contentType(MediaType.APPLICATION_JSON).content(LINK_REQUEST_VALID_CONTENT_BODY))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(LINK_ID_PATH).value(LINK_ID1))
                .andExpect(jsonPath(URL_PATH).value(VALID_LINK));
        }
        //Quota exhausted
        mockMvc.perform(delete(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE)
                .contentType(MediaType.APPLICATION_JSON).content(LINK_REQUEST_VALID_CONTENT_BODY))
            .andExpect(status().isTooManyRequests());
        //Quota restored
        Thread.sleep(THREAD_SLEEP_MS);
        //Successful response
        mockMvc.perform(delete(URL_TEMPLATE).header(HEADER_LABEL, HEADER_VALID_VALUE)
                .contentType(MediaType.APPLICATION_JSON).content(LINK_REQUEST_VALID_CONTENT_BODY))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath(LINK_ID_PATH).value(LINK_ID1))
            .andExpect(jsonPath(URL_PATH).value(VALID_LINK));
    }

}
