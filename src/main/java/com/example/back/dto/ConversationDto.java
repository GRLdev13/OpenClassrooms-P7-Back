package com.example.back.dto;

import java.time.Instant;
import java.util.List;

import com.example.back.domain.Conversation;

public record ConversationDto(
        Long id,
        Long clientId,
        Long adminId,
        String status,
        Instant startDate,
        Instant endDate,
        List<MessageDto> messages) {

    public static ConversationDto fromEntity(Conversation conversation) {
        return fromEntity(conversation, List.of());
    }

    public static ConversationDto fromEntity(Conversation conversation, List<MessageDto> messages) {
        Long clientId = conversation.getClient() == null ? null : conversation.getClient().getId();
        Long adminId = conversation.getAdmin() == null ? null : conversation.getAdmin().getId();

        return new ConversationDto(
                conversation.getId(),
                clientId,
                adminId,
                conversation.getStatus(),
                conversation.getStartDate(),
                conversation.getEndDate(),
                List.copyOf(messages));
    }
}
