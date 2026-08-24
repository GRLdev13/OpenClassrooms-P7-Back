package com.example.back.dto;

import jakarta.validation.constraints.NotNull;

public record CreateConversationRequest(
        @NotNull Long clientId,
        @NotNull Long adminId) {
}
