package com.example.back.dto;

import java.time.Instant;

import com.example.back.domain.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MessageDto(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) Long id,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) Long conversationId,
        Long senderId,
        String senderStatus,
        @NotBlank String content,
        String status,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) Instant creationDate,
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) Instant modificationDate) {

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
