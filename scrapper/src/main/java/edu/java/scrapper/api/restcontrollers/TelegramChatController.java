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

    @PostMapping("/{id}")
    public ResponseEntity<?> chatSignUp(@PathVariable long id) {
        /*
        TODO
        Possible: throw new ConflictException
        Chat registration
         */
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChat(@PathVariable long id) {
        /*
        TODO
        Possible: throw new NotFoundException
        Chat deletion
         */
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
