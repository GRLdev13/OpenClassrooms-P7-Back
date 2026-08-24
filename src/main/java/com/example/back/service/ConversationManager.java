package com.example.back.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.back.domain.Admin;
import com.example.back.domain.Client;
import com.example.back.domain.Conversation;
import com.example.back.domain.Message;
import com.example.back.dto.ConversationDto;
import com.example.back.dto.CreateConversationRequest;
import com.example.back.dto.MessageDto;
import com.example.back.exception.ConversationNotFoundException;
import com.example.back.exception.ConversationParticipantNotFoundException;
import com.example.back.exception.ConversationParticipantsMustDifferException;
import com.example.back.repository.AdminRepository;
import com.example.back.repository.ClientRepository;
import com.example.back.repository.ConversationRepository;
import com.example.back.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConversationManager {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ClientRepository clientRepository;
    private final AdminRepository adminRepository;

    public ConversationManager(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            ClientRepository clientRepository,
            AdminRepository adminRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.clientRepository = clientRepository;
        this.adminRepository = adminRepository;
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

    @Transactional
    public MessageDto createMessage(Long conversationId, MessageDto request) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ConversationNotFoundException(conversationId));

        Instant now = Instant.now();
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(request.senderId());
        message.setSenderStatus(request.senderStatus());
        message.setContent(request.content());
        message.setStatus(request.status());
        message.setCreationDate(now);
        message.setModificationDate(now);

        return MessageDto.fromEntity(messageRepository.save(message));
    }

    @Transactional
    public ConversationDto createConversation(CreateConversationRequest request) {
        if (request.clientId().equals(request.adminId())) {
            throw new ConversationParticipantsMustDifferException();
        }

        Client client = clientRepository.findByIdAndDeletionDateIsNull(request.clientId())
                .orElseThrow(() -> new ConversationParticipantNotFoundException(
                        "Client", request.clientId()));
        Admin admin = adminRepository.findByIdAndDeletionDateIsNull(request.adminId())
                .orElseThrow(() -> new ConversationParticipantNotFoundException(
                        "Admin", request.adminId()));

        Conversation conversation = new Conversation();
        conversation.setClient(client);
        conversation.setAdmin(admin);
        conversation.setStatus("OPEN");
        conversation.setStartDate(Instant.now());

        return ConversationDto.fromEntity(conversationRepository.save(conversation));
    }
}
