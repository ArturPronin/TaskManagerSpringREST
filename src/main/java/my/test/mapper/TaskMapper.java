package my.test.mapper;

import my.test.dto.TaskDTO;
import my.test.dto.TaskSimpleDTO;
import my.test.model.Task;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskDTO toDto(Task task);

    Task toEntity(TaskDTO taskDTO);

    List<TaskDTO> toDtoAll(List<Task> tasks);

    List<Task> toEntityAll(List<TaskDTO> taskDTOs);

    TaskSimpleDTO toSimpleDto(Task task);

    Task toEntitySimple(TaskSimpleDTO taskSimpleDTO);

    List<TaskSimpleDTO> toSimpleDtoAll(List<Task> tasks);

    List<Task> toEntitySimpleAll(List<TaskSimpleDTO> taskSimpleDTOs);
}
