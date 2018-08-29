package com.sbms.greeting.api;

import com.sbms.greeting.api.exceptions.ResourceNotFoundException;
import com.sbms.greeting.domain.Message;
import com.sbms.greeting.domain.repositories.MessageRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageController {
    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/v1/messages")
    public List<Message> getMessages() {
        return messageRepository.findAll();
    }

    @GetMapping("/v1/messages/{language}")
    public Message getMessage(@PathVariable("language") String language) {
        return messageRepository.findByLanguage(language).orElseThrow(ResourceNotFoundException::new);
    }

    @PutMapping("/v1/messages/{language}")
    public Message putMessage(@PathVariable("language") String language, @RequestBody Message message) {
        return messageRepository.findByLanguage(language).orElseGet(() -> messageRepository.save(message));
    }

    @DeleteMapping("/v1/messages/{language}")
    public void deleteMessage(@PathVariable("language") String language) {
        messageRepository.findByLanguage(language).ifPresent(messageRepository::delete);
    }
}
