package com.example.back.dto;

import java.time.Instant;

import com.example.back.domain.Message;

public record MessageDto(
        Long id,
        Long conversationId,
        Long senderId,
        String senderStatus,
        String content,
        String status,
        Instant creationDate,
        Instant modificationDate) {

    public static MessageDto fromEntity(Message message) {
        Long conversationId = message.getConversation() == null
                ? null
                : message.getConversation().getId();

        return new MessageDto(
                message.getId(),
                conversationId,
                message.getSenderId(),
                message.getSenderStatus(),
                message.getContent(),
                message.getStatus(),
                message.getCreationDate(),
                message.getModificationDate());
    }
}
