package edu.java.scrapper.scrapperrestcontrollerstests;

import edu.java.scrapper.api.restcontrollers.LinksRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LinksRestController.class)
class LinksRestControllerTest {

    /*
    TODO
    Add MockBeans in future
     */

    @Autowired
    private MockMvc mockMvc;

}
