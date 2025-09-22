package com.cryy.kanban.service;

import com.cryy.kanban.entities.Task;
import com.cryy.kanban.enums.TaskPriority;
import com.cryy.kanban.enums.TaskStatus;
import com.cryy.kanban.mapper.TaskMapper;
import com.cryy.kanban.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final WebsocketService websocketService;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, WebsocketService websocketService, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.websocketService = websocketService;
        this.taskMapper = taskMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> getAllTasks(TaskStatus status, TaskPriority priority, Pageable pageable) {
        if (status != null && priority != null) {
            return taskRepository.findByStatusAndPriority(status, priority, pageable);
        }

        if (status != null) {
            return taskRepository.findByStatus(status, pageable);
        }

        if (priority != null) {
            return taskRepository.findByPriority(priority, pageable);
        }

        return taskRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
    }

    @Override
    public Task createTask(Task task) {
        Task saved = taskRepository.save(task);
        websocketService.notifyTaskCreated(taskMapper.toResponse(saved));
        return saved;
    }

    @Override
    public Task updateTask(Long id, Task task) {
        Task existing = getTaskById(id);
        task.setId(id);
        task.setVersion(existing.getVersion());
        return taskRepository.save(task);
    }

    @Override
    public Task partialUpdateTask(Long id, Map<String, Object> updates) {
        Task existing = getTaskById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "title" -> existing.setTitle((String) value);
                case "description" -> existing.setDescription((String) value);
                case "status" -> existing.setStatus(TaskStatus.valueOf((String) value));
                case "priority" -> existing.setPriority(TaskPriority.valueOf((String) value));
            }
        });

        return taskRepository.save(existing);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}