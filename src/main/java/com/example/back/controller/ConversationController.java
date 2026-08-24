package com.example.back.controller;

import java.util.List;

import com.example.back.dto.ConversationDto;
import com.example.back.service.ConversationManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/conversations")
@Tag(name = "Conversations", description = "Read conversations and their messages")
public class ConversationController {

    private final ConversationManager conversationManager;

    public ConversationController(ConversationManager conversationManager) {
        this.conversationManager = conversationManager;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a conversation and its linked messages by ID")
    @ApiResponse(responseCode = "200", description = "Conversation found")
    @ApiResponse(responseCode = "404", description = "Conversation not found")
    public ConversationDto getById(@PathVariable Long id) {
        return conversationManager.getById(id);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get all conversations and messages belonging to a client")
    @ApiResponse(responseCode = "200", description = "Client conversations returned")
    public List<ConversationDto> getByClientId(@PathVariable Long clientId) {
        return conversationManager.getByClientId(clientId);
    }
}
