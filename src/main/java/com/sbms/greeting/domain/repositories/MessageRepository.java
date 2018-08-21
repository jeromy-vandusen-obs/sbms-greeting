package com.sbms.greeting.domain.repositories;

import com.sbms.greeting.domain.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MessageRepository extends MongoRepository<Message, String> {
    Optional<Message> findByLanguage(String language);
}
