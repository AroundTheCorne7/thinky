package com.anykey.thinky.service;

import com.anykey.thinky.dto.ChatRequest;
import com.anykey.thinky.dto.ContentResponseDTO;
import com.anykey.thinky.dto.OpenRouterRequest;
import com.anykey.thinky.dto.OpenRouterResponse;
import com.anykey.thinky.model.Conversation;
import com.anykey.thinky.repository.ConversationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenRouterService {

    private final ConversationRepository conversationRepository;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.api.key}")
    private String apiKey;

    public ContentResponseDTO processRequest(ChatRequest chatRequest) {
        try {
            // Set default model if not provided
            if (chatRequest.getModel() == null || chatRequest.getModel().isEmpty()) {
                chatRequest.setModel("google/gemma-3-12b-it:free");
            }

            List<Map<String, String>> messages = new ArrayList<>();

            // Add system message to guide rather than give direct answers
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful assistant that guides users to find answers themselves rather than providing direct solutions. " +
                    "Ask questions to help them think through problems. Provide hints and suggestions rather than complete answers. " +
                    "For math or technical problems, show steps and reasoning approaches but let them reach the final conclusion. " +
                    "Your goal is to teach and facilitate learning, not to solve problems for users.");
            messages.add(systemMessage);

            // Use the history from the request if provided
            // NEW (always load from DB)
            Optional<Conversation> conversationOptional = conversationRepository.findByIdAndUserId(chatRequest.getUserId(), chatRequest.getConversationId());
            Conversation conversation = conversationOptional.orElse(null);
            if (conversation != null) {
                for (Conversation.Message message : conversation.getMessages()) {
                    Map<String, String> messageMap = new HashMap<>();
                    messageMap.put("role", message.getSender().equals("user") ? "user" : "assistant");
                    messageMap.put("content", message.getMessage());
                    messages.add(messageMap);
                }
            } else {
                conversation = Conversation.builder().userId(chatRequest.getUserId()).messages(new ArrayList<>()).build();
            }


            // Save the new user message to the database
            Conversation.Message userMessage = Conversation.Message.builder()
                    .sender("user")
                    .message(chatRequest.getPrompt())
                    .timestamp(LocalDateTime.now())
                    .build();
            conversation.getMessages().add(userMessage);

            // Add current user message
            Map<String, String> currentMessage = new HashMap<>();
            currentMessage.put("role", "user");
            currentMessage.put("content", chatRequest.getPrompt());
            messages.add(currentMessage);

            // Create OpenRouter request
            OpenRouterRequest openRouterRequest = OpenRouterRequest.builder()
                    .model(chatRequest.getModel())
                    .messages(messages)
                    .build();

            // Call OpenRouter API
            OpenRouterResponse response = callOpenRouterApi(openRouterRequest);

            // Extract AI response
            String aiResponse = "";
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                OpenRouterResponse.Choice choice = response.getChoices().getFirst();
                if (choice.getMessage() != null) {
                    aiResponse = choice.getMessage().get("content");
                } else if (choice.getText() != null) {
                    aiResponse = choice.getText();
                }
            }

            // Save AI response to database
            Conversation.Message aiMessage = Conversation.Message.builder()
                    .sender("ai")
                    .message(aiResponse)
                    .timestamp(LocalDateTime.now())
                    .build();
            conversation.getMessages().add(aiMessage);

            conversationRepository.save(conversation);
            // Create metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("model", chatRequest.getModel());

            if (response != null && response.getUsage() != null) {
                metadata.put("promptTokens", response.getUsage().getPromptTokens());
                metadata.put("completionTokens", response.getUsage().getCompletionTokens());
                metadata.put("totalTokens", response.getUsage().getTotalTokens());
            }

            // Return response
            return ContentResponseDTO.builder()
                    .generatedContent(aiResponse)
                    .promptMetadata(metadata)
                    .status(ContentResponseDTO.ResponseStatus.COMPLETED)
                    .build();

        } catch (Exception e) {
            log.error("Error processing request", e);
            return ContentResponseDTO.builder()
                    .generatedContent("Error processing your request")
                    .status(ContentResponseDTO.ResponseStatus.FAILED)
                    .build();
        }
    }

    private OpenRouterResponse callOpenRouterApi(OpenRouterRequest request) throws IOException {
        String requestJson = objectMapper.writeValueAsString(request);

        RequestBody body = RequestBody.create(
                requestJson,
                MediaType.parse("application/json")
        );

        Request httpRequest = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("HTTP-Referer", "http://localhost:8080")
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = httpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                log.error("API call failed with code: {}", response.code());
                throw new IOException("API call failed: " + response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("Empty response from API");
            }

            return objectMapper.readValue(responseBody.string(), OpenRouterResponse.class);
        }
    }
}
