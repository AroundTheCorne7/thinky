package com.anykey.thinky.controller;

import com.anykey.thinky.dto.ChatRequest;
import com.anykey.thinky.dto.ContentResponseDTO;
import com.anykey.thinky.service.OpenRouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AskController {

    private final OpenRouterService openRouterService;

    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody ChatRequest chatRequest) {
        // Validate userId
        if (chatRequest.getUserId() == null || chatRequest.getUserId().isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("userId is required");
        }
        
        // Process the request
        ContentResponseDTO response = openRouterService.processRequest(chatRequest);
        
        return ResponseEntity.ok(response);
    }
}
