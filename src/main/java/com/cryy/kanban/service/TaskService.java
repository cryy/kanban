package com.cryy.kanban.service;

import com.cryy.kanban.entities.Task;
import com.cryy.kanban.enums.TaskPriority;
import com.cryy.kanban.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface TaskService {
    Page<Task> getAllTasks(TaskStatus status, TaskPriority priority, Pageable pageable);
    Task getTaskById(Long id);
    Task createTask(Task task);
    Task updateTask(Long id, Task task);
    Task partialUpdateTask(Long id, Map<String, Object> updates);
    void deleteTask(Long id);
}
