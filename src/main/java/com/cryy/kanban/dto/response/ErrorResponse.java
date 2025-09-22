package com.cryy.kanban.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorResponse  {
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> fieldErrors;

    public ErrorResponse(String message, LocalDateTime timestamp) {
        this(message, timestamp, null);
    }

    public ErrorResponse(String message, LocalDateTime timestamp, Map<String, String> fieldErrors) {
        this.message = message;
        this.timestamp = timestamp;
        this.fieldErrors = fieldErrors;
    }
}