package com.cryy.kanban.controller;

import com.cryy.kanban.dto.request.TaskCreateRequest;
import com.cryy.kanban.dto.response.TaskResponse;
import com.cryy.kanban.entities.Task;
import com.cryy.kanban.enums.TaskPriority;
import com.cryy.kanban.enums.TaskStatus;
import com.cryy.kanban.mapper.TaskMapper;
import com.cryy.kanban.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<Task> tasks = taskService.getAllTasks(status, priority, pageable);
        Page<TaskResponse> response = tasks.map(taskMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(taskMapper.toResponse(task));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        Task task = taskMapper.toEntity(request);
        Task created = taskService.createTask(task);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskCreateRequest request) {

        Task task = taskMapper.toEntityForUpdate(request);
        Task updated = taskService.updateTask(id, task);
        return ResponseEntity.ok(taskMapper.toResponse(updated));
    }

    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<TaskResponse> partialUpdateTask(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        Task updated = taskService.partialUpdateTask(id, updates);
        return ResponseEntity.ok(taskMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
