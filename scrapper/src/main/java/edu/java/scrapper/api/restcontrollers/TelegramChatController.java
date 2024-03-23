package edu.java.scrapper.api.restcontrollers;

import edu.java.scrapper.dao.service.interfaces.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scrapper/tg-chat")
public class TelegramChatController {

    private final static String PATH_VAR_TEMPLATE = "/{id}";
    private final ChatService chatService;

    @Autowired
    public TelegramChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(PATH_VAR_TEMPLATE)
    public ResponseEntity<?> chatSignUp(@PathVariable long id) {
        chatService.register(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(PATH_VAR_TEMPLATE)
    public ResponseEntity<?> deleteChat(@PathVariable long id) {
        chatService.unregister(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
