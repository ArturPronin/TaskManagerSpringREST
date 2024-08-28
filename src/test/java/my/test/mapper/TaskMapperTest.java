package my.test.mapper;

import my.test.dto.TagDTO;
import my.test.dto.TaskDTO;
import my.test.dto.TaskSimpleDTO;
import my.test.dto.UserDTO;
import my.test.model.Tag;
import my.test.model.Task;
import my.test.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskMapperTest {
    private TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);
    private Task task;
    private TaskDTO taskDTO;
    private TaskSimpleDTO taskSimpleDTO;
    private List<Task> taskList;
    private List<TaskDTO> taskDTOList;
    private List<TaskSimpleDTO> taskSimpleDTOList;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setName("User1");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setTitle("Tag 1");

        task = new Task();
        task.setId(1L);
        task.setTitle("Task 1");
        task.setDescription("Description 1");
        task.setUser(user);
        task.setTags(Arrays.asList(tag));

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Task 1");
        taskDTO.setDescription("Description 1");

        taskSimpleDTO = new TaskSimpleDTO();
        taskSimpleDTO.setTitle("Task 1");
        taskSimpleDTO.setDescription("Description 1");

        taskList = Arrays.asList(task);
        taskDTOList = Arrays.asList(taskDTO);
        taskSimpleDTOList = Arrays.asList(taskSimpleDTO);
    }

    @Test
    void toDto() {
        TaskDTO mappedDTO = taskMapper.toDto(task);
        assertEquals(task.getId(), mappedDTO.getId());
        assertEquals(task.getTitle(), mappedDTO.getTitle());
        assertEquals(task.getDescription(), mappedDTO.getDescription());
    }

    @Test
    void toEntity() {
        Task mappedEntity = taskMapper.toEntity(taskDTO);
        assertEquals(taskDTO.getId(), mappedEntity.getId());
        assertEquals(taskDTO.getTitle(), mappedEntity.getTitle());
        assertEquals(taskDTO.getDescription(), mappedEntity.getDescription());
    }

    @Test
    void toSimpleDto() {
        TaskSimpleDTO mappedSimpleDTO = taskMapper.toSimpleDto(task);
        assertEquals(task.getTitle(), mappedSimpleDTO.getTitle());
        assertEquals(task.getDescription(), mappedSimpleDTO.getDescription());
    }

    @Test
    void toEntitySimple() {
        Task taskTest = taskMapper.toEntitySimple(taskSimpleDTO);
        assertEquals(taskSimpleDTO.getTitle(), taskTest.getTitle());
        assertEquals(taskSimpleDTO.getDescription(), taskTest.getDescription());
    }

    @Test
    void toDtoAll() {
        List<TaskDTO> mappedDTOList = taskMapper.toDtoAll(taskList);
        assertEquals(taskList.size(), mappedDTOList.size());
    }

    @Test
    void toEntityAll() {
        List<Task> mappedEntityList = taskMapper.toEntityAll(taskDTOList);
        assertEquals(taskDTOList.size(), mappedEntityList.size());
    }

    @Test
    void toSimpleDtoAll() {
        List<TaskSimpleDTO> mappedSimpleDTOList = taskMapper.toSimpleDtoAll(taskList);
        assertEquals(taskList.size(), mappedSimpleDTOList.size());
    }

    @Test
    void toEntitySimpleAll() {
        List<Task> tasks = taskMapper.toEntitySimpleAll(taskSimpleDTOList);
        assertEquals(taskSimpleDTOList.size(), tasks.size());
    }

    @Test
    void testToDto() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        Tag tag = new Tag();
        tag.setId(1L);
        tag.setTitle("Tag1");

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task Title");
        task.setDescription("Task Description");
        task.setUser(user);
        task.setTags(List.of(tag));

        TaskDTO taskDTO = taskMapper.toDto(task);

        assertEquals(task.getId(), taskDTO.getId());
        assertEquals(task.getTitle(), taskDTO.getTitle());
        assertEquals(task.getDescription(), taskDTO.getDescription());
        assertEquals(task.getUser().getId(), taskDTO.getUser().getId());
        assertEquals(task.getTags().get(0).getTitle(), taskDTO.getTags().get(0).getTitle());
    }

    @Test
    void testToEntity() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("John Doe");

        TagDTO tagDTO = new TagDTO();
        tagDTO.setId(1L);
        tagDTO.setTitle("Tag1");

        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setTitle("Task Title");
        taskDTO.setDescription("Task Description");
        taskDTO.setUser(userDTO);
        taskDTO.setTags(List.of(tagDTO));

        Task task = taskMapper.toEntity(taskDTO);

        assertEquals(taskDTO.getId(), task.getId());
        assertEquals(taskDTO.getTitle(), task.getTitle());
        assertEquals(taskDTO.getDescription(), task.getDescription());
        assertEquals(taskDTO.getUser().getId(), task.getUser().getId());
        assertEquals(taskDTO.getTags().get(0).getTitle(), task.getTags().get(0).getTitle());
    }

    @Test
    void testToSimpleDto() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Simple Task Title");
        task.setDescription("Simple Task Description");

        TaskSimpleDTO taskSimpleDTO = taskMapper.toSimpleDto(task);

        assertEquals(task.getId(), taskSimpleDTO.getId());
        assertEquals(task.getTitle(), taskSimpleDTO.getTitle());
        assertEquals(task.getDescription(), taskSimpleDTO.getDescription());
    }

    @Test
    void testToEntitySimple() {
        TaskSimpleDTO taskSimpleDTO = new TaskSimpleDTO();
        taskSimpleDTO.setId(1L);
        taskSimpleDTO.setTitle("Simple Task Title");
        taskSimpleDTO.setDescription("Simple Task Description");

        Task task = taskMapper.toEntitySimple(taskSimpleDTO);

        assertEquals(taskSimpleDTO.getId(), task.getId());
        assertEquals(taskSimpleDTO.getTitle(), task.getTitle());
        assertEquals(taskSimpleDTO.getDescription(), task.getDescription());
    }


}