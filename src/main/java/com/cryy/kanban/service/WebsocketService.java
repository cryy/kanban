package com.cryy.kanban.service;

import com.cryy.kanban.dto.response.TaskResponse;
import com.cryy.kanban.ws.TaskMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static com.cryy.kanban.ws.TaskEvent.*;

@Service
public class WebsocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebsocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyTaskCreated(TaskResponse task) {
        TaskMessage message = new TaskMessage(CREATE, task);
        messagingTemplate.convertAndSend("/topic/tasks", message);
    }

    public void notifyTaskUpdated(TaskResponse task) {
        TaskMessage message = new TaskMessage(UPDATE, task);
        messagingTemplate.convertAndSend("/topic/tasks", message);
    }

    public void notifyTaskDeleted(Long taskId) {
        TaskMessage message = new TaskMessage(DELETE, taskId);
        messagingTemplate.convertAndSend("/topic/tasks", message);
    }
}
