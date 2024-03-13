package edu.java.scrapper.scrapperrestcontrollerstests;

import edu.java.scrapper.api.restcontrollers.TelegramChatController;
import edu.java.scrapper.dao.service.interfaces.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TelegramChatController.class)
class TelegramChatControllerTest {

    @MockBean
    ChatService chatService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test POST \"chat sign up\" 200 OK")
    void testPostSuccess() throws Exception {
        Mockito.doNothing().when(chatService).register(1L);
        mockMvc.perform(
            post("/scrapper/tg-chat/{id}", 1L)
        ).andExpect(status().isOk());
    }

   // @Test
    @DisplayName("Test POST \"chat sign up\" 400 BadRequest")
    void testPostBadRequest() throws Exception {
        mockMvc.perform(
            post("/scrapper/tg-chat/{id}")
        )
            .andExpect(status().isBadRequest())
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
    @DisplayName("Test DELETE \"chat deletion\" 200 OK")
    void testDeleteSuccess() throws Exception {
        mockMvc.perform(
            delete("/scrapper/tg-chat/{id}", 1L)
        ).andExpect(status().isOk());
    }

}
