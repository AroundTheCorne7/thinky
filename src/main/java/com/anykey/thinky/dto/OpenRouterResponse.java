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
public class OpenRouterResponse {
    
    private String id;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private Map<String, String> message;
        private String text;
        private Integer index;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}
