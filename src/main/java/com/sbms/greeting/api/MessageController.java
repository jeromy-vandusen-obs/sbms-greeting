package com.sbms.greeting.api;

import com.sbms.greeting.api.exceptions.InvalidResourceException;
import com.sbms.greeting.api.exceptions.ResourceNotFoundException;
import com.sbms.greeting.domain.Message;
import com.sbms.greeting.domain.repositories.MessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class MessageController {
    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping(path = "/v1/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message> getMessages() {
        return messageRepository.findAll();
    }

    @GetMapping(path = "/v1/messages/{language}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message getMessage(@PathVariable("language") String language) {
        return messageRepository.findByLanguage(language).orElseThrow(ResourceNotFoundException::new);
    }

    @PutMapping(path = "/v1/messages/{language}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Message putMessage(@PathVariable("language") String language, @RequestBody Message message, HttpServletResponse response) {
        if (! language.equals(message.getLanguage())) {
            throw new InvalidResourceException();
        }
        Message messageToSave = messageRepository.findByLanguage(language).orElse(null);
        if (messageToSave == null) {
            response.setStatus(HttpStatus.CREATED.value());
            return messageRepository.save(message);
        }
        response.setStatus(HttpStatus.OK.value());
        messageToSave.setContent(message.getContent());
        return messageRepository.save(messageToSave);
    }

    @DeleteMapping("/v1/messages/{language}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMessage(@PathVariable("language") String language) {
        messageRepository.delete(messageRepository.findByLanguage(language).orElseThrow(ResourceNotFoundException::new));
    }
}
