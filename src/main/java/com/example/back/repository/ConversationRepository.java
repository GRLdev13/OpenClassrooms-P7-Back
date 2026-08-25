package com.example.back.repository;

import java.util.List;
import java.util.Optional;

import com.example.back.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findAllByClient_IdOrderByStartDateDescIdDesc(Long clientId);

    Optional<Conversation> findFirstByClient_IdAndAdmin_IdOrderByStartDateDescIdDesc(
            Long clientId, Long adminId);
}
