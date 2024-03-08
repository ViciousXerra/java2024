package edu.java.scrapper.scrapperrestcontrollerstests;

import edu.java.scrapper.api.restcontrollers.LinksRestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinksRestController.class)
class LinksRestControllerTest {

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
            .andExpect(content().json("""
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
                }""", false));
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
                    .content("""
                        {
                          "link": "https://stackoverflow.com"
                        }""")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json("""
                {
                  "id": 1,
                  "url": "https://stackoverflow.com"
                }""", false));
    }

    @Test
    @DisplayName("Test POST \"add link\" 400 Bad Request (Missing header)")
    void testPostAddLinkRequestBadRequestMissingHeader() throws Exception {
        mockMvc.perform(
                post("/scrapper/links")
                    .contentType("application/json")
                    .content("""
                        {
                          "link": "not a valid url link"
                        }""")
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
                    .content("""
                        {
                          "link": "not a valid url link"
                        }""")
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
                    .content("""
                        {
                          "link": "https://github.com/ViciousXerra"
                        }""")
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json("""
                {
                  "id": 1,
                  "url": "https://github.com/ViciousXerra"
                }""", false));
    }

    @Test
    @DisplayName("Test DELETE \"remove link\" 400 Bad Request (Missing header)")
    void testDeleteRemoveLinkRequestBadRequestMissingHeader() throws Exception {
        mockMvc.perform(
                post("/scrapper/links")
                    .contentType("application/json")
                    .content("""
                        {
                          "link": "https://github.com/ViciousXerra"
                        }""")
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
                    .content("""
                        {
                          "link": "not a valid url link"
                        }""")
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"));
    }

}
