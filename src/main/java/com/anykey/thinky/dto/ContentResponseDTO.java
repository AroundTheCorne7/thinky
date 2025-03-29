package com.anykey.thinky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponseDTO {
    
    private String generatedContent;
    private Map<String, Object> promptMetadata;
    private ResponseStatus status;
    
    public enum ResponseStatus {
        COMPLETED, FAILED
    }
}
