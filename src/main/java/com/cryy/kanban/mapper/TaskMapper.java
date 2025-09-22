package com.cryy.kanban.mapper;

import com.cryy.kanban.dto.request.TaskCreateRequest;
import com.cryy.kanban.dto.response.TaskResponse;
import com.cryy.kanban.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskResponse toResponse(Task task);
    Task toEntity(TaskCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Task toEntityForUpdate(TaskCreateRequest request);
}