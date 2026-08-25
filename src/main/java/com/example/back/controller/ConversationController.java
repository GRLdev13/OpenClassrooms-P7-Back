package com.example.back.controller;

import java.util.List;

import com.example.back.dto.ConversationDto;
import com.example.back.dto.CreateConversationRequest;
import com.example.back.dto.MessageDto;
import com.example.back.service.ConversationManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @GetMapping("/participants")
    @Operation(summary = "Get the latest conversation between a client and an admin")
    @ApiResponse(responseCode = "200", description = "Conversation returned with its messages")
    @ApiResponse(responseCode = "400", description = "Participant IDs are invalid or identical")
    @ApiResponse(responseCode = "404", description = "Conversation not found")
    public ConversationDto getByParticipantsId(
            @RequestParam Long clientId,
            @RequestParam Long adminId) {
        return conversationManager.getByParticipants(clientId, adminId);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get all conversations and messages belonging to a client")
    @ApiResponse(responseCode = "200", description = "Client conversations returned")
    public List<ConversationDto> getByClientId(@PathVariable Long clientId) {
        return conversationManager.getByClientId(clientId);
    }

    @PostMapping("/{conversationId}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Post a message to a conversation")
    @ApiResponse(responseCode = "201", description = "Message created")
    @ApiResponse(responseCode = "400", description = "Invalid message")
    @ApiResponse(responseCode = "404", description = "Conversation not found")
    public MessageDto createMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody MessageDto request) {
        return conversationManager.createMessage(conversationId, request);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a conversation between a client and an admin")
    @ApiResponse(responseCode = "201", description = "Conversation created")
    @ApiResponse(responseCode = "400", description = "Participant IDs are invalid or identical")
    @ApiResponse(responseCode = "404", description = "Client or admin not found")
    public ConversationDto createConversation(
            @Valid @RequestBody CreateConversationRequest request) {
        return conversationManager.createConversation(request);
    }
}
