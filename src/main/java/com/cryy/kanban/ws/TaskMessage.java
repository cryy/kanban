package com.cryy.kanban.ws;

import lombok.Data;

@Data
public class TaskMessage {
    private TaskEvent eventType;
    private Object data;
    private Long timestamp;

    public TaskMessage() {}

    public TaskMessage(TaskEvent eventType, Object data) {
        this.eventType = eventType;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
}