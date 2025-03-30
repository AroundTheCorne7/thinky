package com.anykey.thinky.repository;

import com.anykey.thinky.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    // For listing all a user's conversations
    List<Conversation> findByUserIdOrderByUpdatedAtDesc(String userId);

    // For accessing a single conversation that belongs to a specific user
    Optional<Conversation> findByIdAndUserId(String conversationId, String userId);
}
