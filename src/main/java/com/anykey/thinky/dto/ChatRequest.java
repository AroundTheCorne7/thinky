package com.anykey.thinky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    
    private String userId;
    private String prompt;
    
    /**
     * Optional conversation history. If provided, this will be used instead of retrieving
     * history from the database. The list should contain alternating messages starting with
     * a user message, then an AI response, and so on.
     */
    @Builder.Default
    private List<String> history = new ArrayList<>();
    
    private String model;
}
