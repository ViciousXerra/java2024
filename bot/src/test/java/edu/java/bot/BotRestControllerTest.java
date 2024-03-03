package edu.java.bot;

import edu.java.bot.api.restcontrollers.BotRestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BotRestController.class)
class BotRestControllerTest {

    /*
    TODO
    Add MockBeans in future
     */

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test success 200 OK")
    void testSuccess() throws Exception {
        mockMvc.perform(
            post("/bot/updates")
                .contentType("application/json")
                .content("""
                    {
                      "id": 1,
                      "url": "https://github.com",
                      "description": "desc",
                      "tgChatIds": [
                        1,
                        2,
                        3
                      ]
                    }""")
        ).andExpect(status().isOk());
    }

    private static String[] provideBadRequestBody() {
        return new String[] {
            """
                    {
                      "id": 1,
                      "url": "not a link",
                      "description": "desc",
                      "tgChatIds": [
                        1,
                        2,
                        3
                      ]
                    }""",
            """
                    {
                      "id": 1,
                      "url": "http://github.com",
                      "description": "",
                      "tgChatIds": [
                        1,
                        2,
                        3
                      ]
                    }""",
            """
                    {
                      "id": 1,
                      "url": "http://github.com",
                      "description": "desc",
                      "tgChatIds": [
                      ]
                    }"""
        };
    }

    @ParameterizedTest
    @MethodSource("provideBadRequestBody")
    @DisplayName("Test 400 Bad Request")
    void testBadRequest(String badRequestBody) throws Exception {
        mockMvc.perform(
                post("/bot/updates")
                    .contentType("application/json")
                    .content(badRequestBody)
            )
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType("application/json"));
    }

}
