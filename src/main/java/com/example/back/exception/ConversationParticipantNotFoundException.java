package com.example.back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConversationParticipantNotFoundException extends RuntimeException {

    public ConversationParticipantNotFoundException(String participantType, Long id) {
        super(participantType + " " + id + " was not found or is inactive");
    }
}
