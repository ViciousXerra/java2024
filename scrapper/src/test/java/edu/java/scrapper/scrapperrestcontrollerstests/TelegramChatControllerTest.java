package edu.java.scrapper.scrapperrestcontrollerstests;

import edu.java.scrapper.api.restcontrollers.TelegramChatController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TelegramChatController.class)
class TelegramChatControllerTest {

    /*
    TODO
    Add MockBeans in future
     */

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test POST success 200 OK")
    void testPostSuccess() throws Exception {
        mockMvc.perform(
            post("/scrapper/tg-chat/{id}", 1L)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test DELETE success 200 OK")
    void testDeleteSuccess() throws Exception {
        mockMvc.perform(
            delete("/scrapper/tg-chat/{id}", 1L)
        ).andExpect(status().isOk());
    }

}
