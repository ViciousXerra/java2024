package edu.java.scrapper.scrapperrestcontrollerstests;

import edu.java.scrapper.api.restcontrollers.LinksRestController;
import edu.java.scrapper.dao.service.interfaces.LinkService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LinksRestController.class)
class LinksRestControllerTest {

    @MockBean
    private LinkService linkService;
    @Autowired
    private MockMvc mockMvc;



}
