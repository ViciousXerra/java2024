package edu.java.scrapper.scrapperrestcontrollerstests;

import edu.java.scrapper.api.restcontrollers.LinksRestController;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinksRestController.class)
class LinksRestControllerTest {

    @MockBean
    LinkService linkService;

    private static final String GET_RESPONSE_BODY =
        """
        {
            "links": [
                {
                    "id": 1,
                    "url": "https://github.com/ViciousXerra"
                },
                {
                    "id": 1,
                    "url": "https://stackoverflow.com"
                }
            ],
            "size": 2
        }
        """;
    private static final String INVALID_LINK_REQUEST_BODY =
        """
        {
            "link": "not a valid url link"
        }
        """;
    public static final String VALID_LINK_REQUEST_BODY =
        """
        {
            "link": "https://github.com/ViciousXerra"
        }
        """;
    public static final String VALID_RESPONSE_BODY1 = """
        {
            "id": 1,
            "url": "https://stackoverflow.com"
        }
        """;
    public static final String VALID_RESPONSE_BODY2 = """
        {
            "id": 1,
            "url": "https://github.com/ViciousXerra"
        }
        """;

    /*
    TODO
    Add MockBeans in future
     */

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test GET \"get all links\" 200 OK")
    void testGetAllLinksSuccess() throws Exception {
        mockMvc.perform(
                get("/scrapper/links").header("Tg-Chat-Id", 1L)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(GET_RESPONSE_BODY, false));
    }

    @Test
    @DisplayName("Test GET \"get all links\" 400 Bad Request (Missing header)")
    void testGetAllLinksBadRequestMissingHeader() throws Exception {
        mockMvc.perform(
                get("/scrapper/links")
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Test POST \"add link\" 200 OK")
    void testPostAddLinkRequestSuccess() throws Exception {
        mockMvc.perform(
                post("/scrapper/links")
                    .header("Tg-Chat-Id", 1L)
                    .contentType("application/json")
                    .content(VALID_LINK_REQUEST_BODY)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(VALID_RESPONSE_BODY1, false));
    }

    @Test
    @DisplayName("Test POST \"add link\" 400 Bad Request (Missing header)")
    void testPostAddLinkRequestBadRequestMissingHeader() throws Exception {
        mockMvc.perform(
                post("/scrapper/links")
                    .contentType("application/json")
                    .content(
                        INVALID_LINK_REQUEST_BODY)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Test POST \"add link\" 400 Bad Request")
    void testPostAddLinkRequestBadRequest() throws Exception {
        mockMvc.perform(
                post("/scrapper/links")
                    .header("Tg-Chat-Id", 1L)
                    .contentType("application/json")
                    .content(
                        INVALID_LINK_REQUEST_BODY)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Test DELETE \"remove link\" 200 OK")
    void testDeleteRemoveLinkRequestSuccess() throws Exception {
        mockMvc.perform(
                delete("/scrapper/links")
                    .header("Tg-Chat-Id", 1L)
                    .contentType("application/json")
                    .content(VALID_LINK_REQUEST_BODY)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(VALID_RESPONSE_BODY2, false));
    }

    @Test
    @DisplayName("Test DELETE \"remove link\" 400 Bad Request (Missing header)")
    void testDeleteRemoveLinkRequestBadRequestMissingHeader() throws Exception {
        mockMvc.perform(
                post("/scrapper/links")
                    .contentType("application/json")
                    .content(
                        VALID_LINK_REQUEST_BODY)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("Test DELETE \"remove link\" 400 Bad Request")
    void testDeleteRemoveLinkRequestBadRequest() throws Exception {
        mockMvc.perform(
                post("/scrapper/links")
                    .header("Tg-Chat-Id", 1L)
                    .contentType("application/json")
                    .content(
                        INVALID_LINK_REQUEST_BODY)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"));
    }

}
