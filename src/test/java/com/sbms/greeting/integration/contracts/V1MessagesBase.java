package com.sbms.greeting.integration.contracts;

import com.sbms.greeting.api.MessageController;
import com.sbms.greeting.domain.Message;
import com.sbms.greeting.domain.repositories.MessageRepository;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public abstract class V1MessagesBase {
    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageController messageController;

    @Before
    public void initialize() {
        when(messageRepository.findAll()).thenReturn(Arrays.asList(
                new Message("1", "en", "Hello World"),
                new Message("2", "fr", "Bonjour Monde"),
                new Message("3", "pt", "Olá Mondo")
        ));
        when(messageRepository.findByLanguage("en")).thenReturn(Optional.of(new Message("1", "en", "Hello World")));
        when(messageRepository.findByLanguage("es")).thenReturn(Optional.empty());
        when(messageRepository.findByLanguage("ru")).thenReturn(Optional.empty());
        when(messageRepository.save(eq(new Message("1", "en", "Hello World!")))).thenReturn(new Message("1", "en", "Hello World!"));
        when(messageRepository.save(eq(new Message(null, "es", "Hola Mundo")))).thenReturn(new Message("4", "es", "Hola Mundo"));
        RestAssuredMockMvc.standaloneSetup(MockMvcBuilders.standaloneSetup(messageController));
    }
}
