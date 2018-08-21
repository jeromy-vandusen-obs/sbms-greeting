package com.sbms.greeting.domain;

import org.springframework.data.annotation.Id;

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
}
