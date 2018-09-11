package com.sbms.greeting.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "messages")
public class Message {
    @Id
    private String id;

    private String language;

    private String content;

    public Message() {
        super();
    }

    public Message(String id, String language, String content) {
        this.id = id;
        this.language = language;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) &&
                Objects.equals(language, message.language) &&
                Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, language, content);
    }
}
