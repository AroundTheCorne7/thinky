package com.anykey.thinky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenRouterRequest {
    
    private String model;
    private List<Map<String, String>> messages;
    
    @Builder.Default
    private Double temperature = 0.7; // Higher temperature for more exploratory responses
    
    @Builder.Default
    private Integer max_tokens = 1000; // Ensure responses are detailed enough for guidance
}
