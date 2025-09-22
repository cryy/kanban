package com.cryy.kanban.dto.response;

import com.cryy.kanban.enums.TaskPriority;
import com.cryy.kanban.enums.TaskStatus;
import lombok.Data;

@Data
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long createdAt;
    private Long updatedAt;

    public TaskResponse() {}
}
