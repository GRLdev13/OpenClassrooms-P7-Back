package com.example.back.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.back.domain.Conversation;
import com.example.back.dto.ConversationDto;
import com.example.back.dto.MessageDto;
import com.example.back.exception.ConversationNotFoundException;
import com.example.back.repository.ConversationRepository;
import com.example.back.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConversationManager {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationManager(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public ConversationDto getById(Long id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException(id));

        List<MessageDto> messages = messageRepository
                .findAllByConversation_IdOrderByCreationDateAscIdAsc(id)
                .stream()
                .map(MessageDto::fromEntity)
                .toList();

        return ConversationDto.fromEntity(conversation, messages);
    }

    public List<ConversationDto> getByClientId(Long clientId) {
        List<Conversation> conversations = conversationRepository
                .findAllByClient_IdOrderByStartDateDescIdDesc(clientId);

        Map<Long, List<MessageDto>> messagesByConversationId = messageRepository
                .findAllByConversation_Client_IdOrderByConversation_IdAscCreationDateAscIdAsc(clientId)
                .stream()
                .collect(Collectors.groupingBy(
                        message -> message.getConversation().getId(),
                        Collectors.mapping(MessageDto::fromEntity, Collectors.toList())));

        return conversations.stream()
                .map(conversation -> ConversationDto.fromEntity(
                        conversation,
                        messagesByConversationId.getOrDefault(conversation.getId(), List.of())))
                .toList();
    }
}
