package com.example.back.controller;

import java.time.Instant;

import com.example.back.domain.Admin;
import com.example.back.domain.Client;
import com.example.back.domain.Conversation;
import com.example.back.domain.Message;
import com.example.back.repository.AdminRepository;
import com.example.back.repository.ClientRepository;
import com.example.back.repository.ConversationRepository;
import com.example.back.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ConversationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void getsAConversationWithItsMessagesInChronologicalOrder() throws Exception {
        Client client = new Client();
        client.setMail("conversation@example.com");
        client = clientRepository.save(client);

        Conversation conversation = new Conversation();
        conversation.setClient(client);
        conversation.setStatus("OPEN");
        conversation.setStartDate(Instant.parse("2026-08-24T08:00:00Z"));
        conversation = conversationRepository.save(conversation);

        saveMessage(conversation, "Second message", "2026-08-24T08:02:00Z");
        saveMessage(conversation, "First message", "2026-08-24T08:01:00Z");

        mockMvc.perform(get("/api/conversations/{id}", conversation.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(conversation.getId()))
                .andExpect(jsonPath("$.clientId").value(client.getId()))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.messages.length()").value(2))
                .andExpect(jsonPath("$.messages[0].content").value("First message"))
                .andExpect(jsonPath("$.messages[1].content").value("Second message"))
                .andExpect(jsonPath("$.messages[0].conversationId").value(conversation.getId()));
    }

    @Test
    void returnsNotFoundForAnUnknownConversation() throws Exception {
        mockMvc.perform(get("/api/conversations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void getsOnlyConversationsBelongingToTheRequestedClient() throws Exception {
        Client requestedClient = saveClient("requested@example.com");
        Client anotherClient = saveClient("another@example.com");

        Conversation olderConversation = saveConversation(
                requestedClient, "CLOSED", "2026-08-22T08:00:00Z");
        Conversation newerConversation = saveConversation(
                requestedClient, "OPEN", "2026-08-24T08:00:00Z");
        Conversation unrelatedConversation = saveConversation(
                anotherClient, "OPEN", "2026-08-25T08:00:00Z");

        saveMessage(olderConversation, "Older client message", "2026-08-22T08:01:00Z");
        saveMessage(newerConversation, "Newer client message", "2026-08-24T08:01:00Z");
        saveMessage(unrelatedConversation, "Must not be returned", "2026-08-25T08:01:00Z");

        mockMvc.perform(get("/api/conversations/client/{clientId}", requestedClient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(newerConversation.getId()))
                .andExpect(jsonPath("$[0].clientId").value(requestedClient.getId()))
                .andExpect(jsonPath("$[0].messages[0].content").value("Newer client message"))
                .andExpect(jsonPath("$[1].id").value(olderConversation.getId()))
                .andExpect(jsonPath("$[1].messages[0].content").value("Older client message"));
    }

    @Test
    void returnsAnEmptyListWhenAClientHasNoConversations() throws Exception {
        Client client = saveClient("without-conversation@example.com");

        mockMvc.perform(get("/api/conversations/client/{clientId}", client.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getsTheLatestConversationByParticipantIdsWithItsMessages() throws Exception {
        Client client = saveClient("participant-client@example.com");
        Admin admin = saveAdmin("participant-admin@example.com");
        if (admin.getId().equals(client.getId())) {
            admin = saveAdmin("participant-admin-2@example.com");
        }

        Conversation olderConversation = saveConversation(
                client, admin, "CLOSED", "2026-08-22T08:00:00Z");
        Conversation latestConversation = saveConversation(
                client, admin, "OPEN", "2026-08-24T08:00:00Z");
        saveMessage(olderConversation, "Old conversation message", "2026-08-22T08:01:00Z");
        saveMessage(latestConversation, "Second message", "2026-08-24T08:02:00Z");
        saveMessage(latestConversation, "First message", "2026-08-24T08:01:00Z");

        mockMvc.perform(get("/api/conversations/participants")
                        .param("clientId", client.getId().toString())
                        .param("adminId", admin.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(latestConversation.getId()))
                .andExpect(jsonPath("$.clientId").value(client.getId()))
                .andExpect(jsonPath("$.adminId").value(admin.getId()))
                .andExpect(jsonPath("$.messages.length()").value(2))
                .andExpect(jsonPath("$.messages[0].content").value("First message"))
                .andExpect(jsonPath("$.messages[1].content").value("Second message"));
    }

    @Test
    void returnsNotFoundWhenParticipantsHaveNoConversation() throws Exception {
        Client client = saveClient("unmatched-client@example.com");
        Admin admin = saveAdmin("unmatched-admin@example.com");
        if (admin.getId().equals(client.getId())) {
            admin = saveAdmin("unmatched-admin-2@example.com");
        }

        mockMvc.perform(get("/api/conversations/participants")
                        .param("clientId", client.getId().toString())
                        .param("adminId", admin.getId().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsIdenticalParticipantIdsWhenGettingAConversation() throws Exception {
        mockMvc.perform(get("/api/conversations/participants")
                        .param("clientId", "77")
                        .param("adminId", "77"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postsAMessageUsingTheMessageDtoStructure() throws Exception {
        Client client = saveClient("message-author@example.com");
        Conversation conversation = saveConversation(
                client, "OPEN", "2026-08-24T08:00:00Z");

        mockMvc.perform(post("/api/conversations/{conversationId}/messages", conversation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": 999,
                                  "conversationId": 999,
                                  "senderId": 42,
                                  "senderStatus": "CLIENT",
                                  "content": "A new message",
                                  "status": "SENT",
                                  "creationDate": "2000-01-01T00:00:00Z",
                                  "modificationDate": "2000-01-01T00:00:00Z"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.conversationId").value(conversation.getId()))
                .andExpect(jsonPath("$.senderId").value(42))
                .andExpect(jsonPath("$.senderStatus").value("CLIENT"))
                .andExpect(jsonPath("$.content").value("A new message"))
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.creationDate").exists())
                .andExpect(jsonPath("$.modificationDate").exists());

        Message savedMessage = messageRepository
                .findAllByConversation_IdOrderByCreationDateAscIdAsc(conversation.getId())
                .getFirst();
        assertThat(savedMessage.getConversation().getId()).isEqualTo(conversation.getId());
        assertThat(savedMessage.getCreationDate()).isNotEqualTo(Instant.parse("2000-01-01T00:00:00Z"));
    }

    @Test
    void rejectsAMessageForAnUnknownConversation() throws Exception {
        mockMvc.perform(post("/api/conversations/{conversationId}/messages", Long.MAX_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "senderId": 42,
                                  "senderStatus": "CLIENT",
                                  "content": "A new message",
                                  "status": "SENT"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void createsAConversationBetweenAnExistingClientAndAdmin() throws Exception {
        Client client = saveClient("new-conversation@example.com");
        Admin admin = saveAdmin("agent@example.com");
        if (admin.getId().equals(client.getId())) {
            admin = saveAdmin("second-agent@example.com");
        }

        mockMvc.perform(post("/api/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": %d,
                                  "adminId": %d
                                }
                                """.formatted(client.getId(), admin.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.clientId").value(client.getId()))
                .andExpect(jsonPath("$.adminId").value(admin.getId()))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.startDate").exists())
                .andExpect(jsonPath("$.endDate").isEmpty())
                .andExpect(jsonPath("$.messages.length()").value(0));

        Conversation savedConversation = conversationRepository.findAll().getLast();
        assertThat(savedConversation.getClient().getId()).isEqualTo(client.getId());
        assertThat(savedConversation.getAdmin().getId()).isEqualTo(admin.getId());
    }

    @Test
    void rejectsIdenticalClientAndAdminIds() throws Exception {
        mockMvc.perform(post("/api/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": 77,
                                  "adminId": 77
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsAConversationWhenAParticipantDoesNotExist() throws Exception {
        Client client = saveClient("client-without-admin@example.com");

        mockMvc.perform(post("/api/conversations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "clientId": %d,
                                  "adminId": %d
                                }
                                """.formatted(client.getId(), Long.MAX_VALUE)))
                .andExpect(status().isNotFound());
    }

    private Client saveClient(String mail) {
        Client client = new Client();
        client.setMail(mail);
        return clientRepository.save(client);
    }

    private Admin saveAdmin(String mail) {
        Admin admin = new Admin();
        admin.setMail(mail);
        return adminRepository.save(admin);
    }

    private Conversation saveConversation(Client client, String status, String startDate) {
        return saveConversation(client, null, status, startDate);
    }

    private Conversation saveConversation(
            Client client, Admin admin, String status, String startDate) {
        Conversation conversation = new Conversation();
        conversation.setClient(client);
        conversation.setAdmin(admin);
        conversation.setStatus(status);
        conversation.setStartDate(Instant.parse(startDate));
        return conversationRepository.save(conversation);
    }

    private void saveMessage(Conversation conversation, String content, String creationDate) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setContent(content);
        message.setCreationDate(Instant.parse(creationDate));
        messageRepository.save(message);
    }
}
