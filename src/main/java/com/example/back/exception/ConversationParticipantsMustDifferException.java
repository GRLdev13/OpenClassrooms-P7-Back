package com.example.back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConversationParticipantsMustDifferException extends RuntimeException {

    public ConversationParticipantsMustDifferException() {
        super("Client and admin IDs must be different");
    }
}
