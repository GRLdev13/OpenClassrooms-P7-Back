package com.example.back.repository;

import java.util.List;

import com.example.back.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findAllByConversation_IdOrderByCreationDateAscIdAsc(Long conversationId);

    List<Message> findAllByConversation_Client_IdOrderByConversation_IdAscCreationDateAscIdAsc(
            Long clientId);
}
