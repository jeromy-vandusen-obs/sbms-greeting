package com.sbms.greeting.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidResourceException extends RuntimeException {
    public InvalidResourceException() {
        super();
    }
}
