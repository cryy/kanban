package com.cryy.kanban.test;

import com.cryy.kanban.entities.Task;
import com.cryy.kanban.mapper.TaskMapper;
import com.cryy.kanban.repository.TaskRepository;
import com.cryy.kanban.service.TaskServiceImpl;
import com.cryy.kanban.service.WebsocketService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    private final TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private WebsocketService websocketService;

    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository, websocketService, taskMapper);
    }

    @Test
    void createTask_ShouldReturnSavedTask() {
        Task task = new Task("Test Task", "Description");
        Task savedTask = new Task("Test Task", "Description");
        savedTask.setId(1L);

        when(taskRepository.save(task)).thenReturn(savedTask);

        Task result = taskService.createTask(task);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Task");
        verify(taskRepository).save(task);
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        Long taskId = 1L;
        Task task = new Task("Test Task", "Description");
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(taskId);

        assertThat(result.getId()).isEqualTo(taskId);
    }

    @Test
    void getTaskById_WhenTaskNotExists_ShouldThrowException() {
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(taskId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found with id: 1");
    }
}
