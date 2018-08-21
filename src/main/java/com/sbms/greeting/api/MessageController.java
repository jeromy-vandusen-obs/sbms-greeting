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

    @GetMapping("/messages")
    public List<Message> messages() {
        return messageRepository.findAll();
    }

    @PostMapping("/messages")
    public Message message(@RequestBody Message message) {
        return messageRepository.save(message);
    }

    @GetMapping("/messages/{language}")
    public Message message(@PathVariable("language") String language) {
        return messageRepository.findByLanguage(language).orElseThrow(ResourceNotFoundException::new);
    }
}
