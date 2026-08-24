package com.example.back.repository;

import java.util.List;

import com.example.back.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findAllByClient_IdOrderByStartDateDescIdDesc(Long clientId);
}
