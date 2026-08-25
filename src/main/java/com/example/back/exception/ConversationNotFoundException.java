package com.example.back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConversationNotFoundException extends RuntimeException {

    public ConversationNotFoundException(Long id) {
        super("Conversation " + id + " was not found");
    }

    public ConversationNotFoundException(Long clientId, Long adminId) {
        super("No conversation was found for client " + clientId + " and admin " + adminId);
    }
}
