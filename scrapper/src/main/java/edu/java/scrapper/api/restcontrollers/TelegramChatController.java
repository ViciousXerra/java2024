package edu.java.scrapper.api.restcontrollers;

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

    @PostMapping(PATH_VAR_TEMPLATE)
    public ResponseEntity<?> chatSignUp(@PathVariable long id) {
        /*
        TODO
        Possible: throw new ConflictException
        Chat registration
         */
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(PATH_VAR_TEMPLATE)
    public ResponseEntity<?> deleteChat(@PathVariable long id) {
        /*
        TODO
        Possible: throw new NotFoundException
        Chat deletion
         */
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
